package mcnc.survwey.api.survey.manage.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.survey.repository.SurveyRepository;
import mcnc.survwey.api.survey.manage.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.service.QuestionService;
import mcnc.survwey.domain.selection.service.SelectionService;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.domain.survey.service.SurveyRedisService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


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
     *
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
        //질문과 보기 저장
        saveQuestionAndSelection(surveyWithDetailDTO, createdSurvey);
        return createdSurvey;
    }

    /**
     * 질문과 보기 저장 메서드
     *
     * @param surveyWithDetailDTO
     * @param survey
     */
    public void saveQuestionAndSelection(SurveyWithDetailDTO surveyWithDetailDTO, Survey survey) {
        surveyWithDetailDTO.getQuestionList()
                .forEach(questionDTO -> {
                    //질문 생성 후 저장
                    Question createdQuestion = questionService.buildAndSaveQuestion(questionDTO, survey);
                    //보기 생성 후 저장
                    selectionService.buildAndSaveSelection(createdQuestion, questionDTO.getSelectionList());
                });
    }

    /**
     * 설문 삭제(오버로딩)
     * - 요청자와 설문 생성자가 일치할시에만 삭제
     * - 설문 삭제 시 설문 캐시도 삭제
     *
     * @param userId
     * @param surveyId
     */
    @Transactional
    @CacheEvict(value = "survey")
    public void deleteSurveyAfterValidation(String userId, Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        surveyService.validateUserMadeSurvey(userId, survey);
        surveyRedisService.deleteSurveyFromRedis(userId, surveyId);
        surveyRepository.delete(survey);
    }


    /**
     * 설문 수정
     * - 설문의 기존 값 업데이트
     * - 기존 설문에 관련된 질문/보기 삭제 후 새롭게 만들어 저장
     *
     * @param surveyWithDetailDTO
     * @param userId
     * @return - 이미 응답한 사용자가 있는 경우 에러
     * - 해당 아이디의 설문이 존재하지 않으면 에러
     * - 수정자와 생성자가 일치하지 않으면 에러
     */
    @Transactional
    public SurveyWithDetailDTO modifySurvey(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        Survey existingSurvey = surveyService.findBySurveyId(surveyWithDetailDTO.getSurveyId());
        //설문이 수정 가능한지 확인
        checkSurveyModifiability(existingSurvey, userId);

        //Redis 만료 시간 재설정
        surveyRedisService.resetExpireTime(userId, existingSurvey.getSurveyId(), surveyWithDetailDTO.getExpireDate());

        //설문 값 업데이트
        surveyService.updateSurveyData(existingSurvey, surveyWithDetailDTO);

        //질문과 보기 삭제(Cascade여서 질문 삭제 시 보기 자동 삭제)
        questionService.deleteBySurveyId(existingSurvey.getSurveyId());

        //질문과 보기 저장
        saveQuestionAndSelection(surveyWithDetailDTO, existingSurvey);

        return SurveyWithDetailDTO.of(existingSurvey);

    }

    /**
     * 설문이 수정 가능한지 확인
     * - 응답을 했는지 확인
     * - 요청자가 생성자가 아닌지
     * - 만료일이 지나지 않았는지
     * @param survey
     * @param userId
     * 하나라도 만족 못하면 메서드 자체에서 400 에러 응답
     */
    public void checkSurveyModifiability(Survey survey, String userId) {
        //요청자가 생성자가 아니면 에러
        surveyService.validateUserMadeSurvey(userId, survey);
        //응답을 했는지 확인
        respondService.existsBySurveyId(survey.getSurveyId());
        //만료일 이후면 수정 불가
        surveyService.checkSurveyExpiration(survey.getExpireDate());
    }

    /**
     * 설문 강제 종료
     * 만료일을 현재로 변경하여 설문 종료
     *
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
