package mcnc.survwey.domain.survey.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.dto.QuestionResultDTO;
import mcnc.survwey.domain.question.dto.ResponseDTO;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.user.dto.AgeCountDTO;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final SurveyService surveyService;
    private final UserService userService;

    public Page<SurveyWithCountDTO> getUserCreatedSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> surveyPageList = surveyRepository.findSurveyListWithRespondCountByUserId(userId, pageable);
        return surveyPageList.map(SurveyWithCountDTO::of);
    }


    public Page<SurveyDTO> getUserRespondSurveyList(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findRespondedSurveyByUserId(userId, pageable);
    }

    public SurveyWithDetailDTO getSurveyWithDetail(Long surveyId) {
        return Optional.ofNullable(surveyRepository.getSurveyWithDetail(surveyId))
                .map(SurveyWithDetailDTO::of)
                .orElse(null);
    }

    /**
     * 설문 검색
     *
     * @param userId
     * @param title
     * @param page
     * @param size
     * @return
     */
    public Page<Survey> surveySearch(String userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findByUser_UserIdAndTitleContainingIgnoreCase(userId, title, pageable);
    }

    public Page<Survey> respondedSurveySearch(String userId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return surveyRepository.findSurveysUserHasRespondedTo(userId, title, pageable);
    }


    public SurveyResultDTO getSurveyResponse(Long surveyId, String userId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        surveyService.verifyUserMadeSurvey(userId, survey);
        List<ResponseDTO> responseDTOList = questionRepository.findQuestionsAndAnswersBySurveyId(surveyId)
                .stream().map(ResponseDTO::new).toList();

        SurveyResultDTO surveyResultDTO = SurveyResultDTO.of(survey);
        setAgeAndGenderCount(surveyId, surveyResultDTO);

        Map<Long, QuestionResultDTO> questionMap = new LinkedHashMap<>();

        for (ResponseDTO responseDTO : responseDTOList) {
            updateQuestionResult(responseDTO, questionMap, surveyResultDTO);
            addResponsesToQuestion(responseDTO, questionMap.get(responseDTO.getQuesId()));
        }
        return surveyResultDTO;
    }

    private void updateQuestionResult(ResponseDTO responseDTO, Map<Long, QuestionResultDTO> questionMap, SurveyResultDTO surveyResultDTO) {
        Long quesId = responseDTO.getQuesId();
        if (!questionMap.containsKey(quesId)) {
            QuestionResultDTO questionResult = new QuestionResultDTO(responseDTO);
            questionMap.put(quesId, questionResult);
            surveyResultDTO.getQuestionList().add(questionResult);
        }
    }

    private void addResponsesToQuestion(ResponseDTO responseDTO, QuestionResultDTO question) {
        QuestionType questionType = responseDTO.getQuestionType();
        switch (questionType) {
            case OBJ_MULTI:
            case OBJ_SINGLE:
                SelectionResultDTO selection = new SelectionResultDTO(responseDTO);
                if (responseDTO.getIsEtc() && responseDTO.getEtcAnswer() != null) {
                    selection.getEtcAnswer().add(responseDTO.getEtcAnswer());
                }
                question.getSelectionList().add(selection);
                break;
            case SUBJECTIVE:
                if (responseDTO.getSubjectiveResponse() != null) {
                    question.getSubjAnswerList().add(responseDTO.getSubjectiveResponse());
                }
                break;
            default:
                //TO-DO 예외처리 로직 작성
                break;
        }
    }

    private void setAgeAndGenderCount(Long surveyId, SurveyResultDTO surveyResultDTO) {
        List<GenderCountDTO> genderCountDTOList = userService.getGenderCountListBySurveyId(surveyId);
        List<AgeCountDTO> ageCountDTOList = userService.getAgeGroupCountBySurveyId(surveyId);

        surveyResultDTO.setAgeCountList(ageCountDTOList);
        surveyResultDTO.setGenderCountList(genderCountDTOList);
    }

}
