package mcnc.survwey.domain.survey.manage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.service.QuestionService;
import mcnc.survwey.domain.selection.service.SelectionService;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import mcnc.survwey.domain.survey.common.service.SurveyRedisService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyManageService {

    private final SurveyService surveyService;
    private final QuestionService questionService;
    private final SelectionService selectionService;
    private final UserService userService;
    private final RespondService respondService;
    private final SurveyRepository surveyRepository;
    private final SurveyRedisService surveyRedisService;

    /**
     * 설문 상세(설문, 질문, 보기) 저장
     * - 설문, 질문, 보기 생성 후 저장
     * @param surveyWithDetailDTO
     * @param userId
     * @return
     */
    @Transactional
    public Survey saveSurveyWithDetails(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        User creator = userService.findByUserId(userId);
        //설문 생성 후 저장
        Survey createdSurvey = surveyService.buildAndSaveSurvey(surveyWithDetailDTO, creator);
        //Redis에 Key 저장
        surveyRedisService.saveSurveyExpireTime(createdSurvey.getSurveyId(), creator.getUserId(), createdSurvey.getExpireDate());
        surveyWithDetailDTO.getQuestionList()
                .forEach(questionDTO -> {
                    //질문 생성 후 저장
                    Question createdQuestion = questionService.buildAndSaveQuestion(questionDTO, createdSurvey);
                    //보기 생성 후 저장
                    selectionService.buildAndSaveSelection(createdQuestion, questionDTO.getSelectionList());
                });
        return createdSurvey;
    }

    /**
     * 설문 삭제(오버로딩)
     * - 요청자와 설문 생성자가 일치할시에만 삭제
     * @param userId
     * @param surveyId
     */
    @Transactional
    public void deleteSurveyAfterValidation(String userId, Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        surveyService.validateUserMadeSurvey(userId, survey);
        surveyRedisService.deleteSurveyFromRedis(userId, surveyId);
        surveyRepository.delete(survey);
    }

    /**
     * 설문 삭제(오버로딩)
     * - 요청자와 설문 생성자가 일치할시에만 삭제
     * @param userId
     * @param survey
     */
    public void deleteSurveyAfterValidation(String userId, Survey survey) {
        surveyService.validateUserMadeSurvey(userId, survey);
        surveyRedisService.deleteSurveyFromRedis(userId, survey.getSurveyId());
        surveyRepository.delete(survey);
    }


    /**
     * 설문 수정
     * - 설문의 생성일은 변경하지 않음
     * - 기존 설문 삭제 후 새롭게 만들어 저장
     * @param surveyWithDetailDTO
     * @param userId
     * @return
     * - 이미 응답한 사용자가 있는 경우 에러
     * - 해당 아이디의 설문이 존재하지 않으면 에러
     * - 수정자와 생성자가 일치하지 않으면 에러
     */
    @Transactional
    public SurveyWithDetailDTO modifySurvey(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        //설문 응답자가 존재하면 error
        respondService.existsBySurveyId(surveyWithDetailDTO.getSurveyId());
        Survey existingSurvey = surveyService.findBySurveyId(surveyWithDetailDTO.getSurveyId());

        //삭제(존재하는지 확인 및 생성자 검증 후)
        deleteSurveyAfterValidation(userId, existingSurvey);

        //생성일은 처음과 같이 고정
        surveyWithDetailDTO.setCreateDate(existingSurvey.getCreateDate());
        //다시 저장
        Survey survey = Optional.ofNullable(saveSurveyWithDetails(surveyWithDetailDTO, userId))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
        return SurveyWithDetailDTO.of(survey);

    }

    /**
     * 설문 강제 종료
     * 만료일을 현재로 변경하여 설문 종료
     * @param userId
     * @param surveyId
     */
    @Transactional
    public void enforceCloseSurvey(String userId, Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        //본인이 만든 설문인지 검증
        surveyService.validateUserMadeSurvey(userId, survey);
        survey.setExpireDate(LocalDateTime.now());
        surveyRedisService.expireImmediately(userId, surveyId);
        //만료일 현재로 변경
        surveyRepository.save(survey);
    }

}
