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


    @Transactional
    public Survey saveSurveyWithDetails(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        User creator = userService.findByUserId(userId);
        Survey createdSurvey = surveyService.buildAndSaveSurvey(surveyWithDetailDTO, creator);
        surveyWithDetailDTO.getQuestionList()
                .forEach(questionDTO -> {
                    Question createdQuestion = questionService.buildAndSaveQuestion(questionDTO, createdSurvey);
                    selectionService.buildAndSaveSelection(createdQuestion, questionDTO.getSelectionList());
                });
        return createdSurvey;
    }

    public void deleteSurvey(String userId, Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        surveyService.verifyUserMadeSurvey(userId, survey);
        surveyRepository.delete(survey);
    }


    /**
     * 설문 수정
     *
     * @param surveyWithDetailDTO
     * @param userId
     * @return
     */
    @Transactional
    public SurveyWithDetailDTO surveyModifyWithDetails(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        respondService.existsBySurveyId(surveyWithDetailDTO.getSurveyId());
        //설문 응답자가 존재하면 error

        log.info(surveyWithDetailDTO.toString());
        //삭제(존재하는지 확인 및 생성자 검증 후)
        deleteSurvey(userId, surveyWithDetailDTO.getSurveyId());
        //다시 저장
        Survey survey = Optional.ofNullable(saveSurveyWithDetails(surveyWithDetailDTO, userId))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
        return SurveyWithDetailDTO.of(survey);

    }

    @Transactional
    public void enforceCloseSurvey(String userId, Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        //본인이 만든 설문인지 검증
        surveyService.verifyUserMadeSurvey(userId, survey);
        survey.setExpireDate(LocalDateTime.now());
        //만료일 현재로 변경
        surveyRepository.save(survey);
    }

}
