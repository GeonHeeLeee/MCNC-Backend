package mcnc.survwey.domain.survey.response.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import mcnc.survwey.domain.objAnswer.service.ObjAnswerService;
import mcnc.survwey.domain.question.dto.QuestionResultDTO;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.respond.dto.ResponseDTO;
import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.subjAnswer.service.SubjAnswerService;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.survey.response.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyResponseDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.AgeCountDTO;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyResponseService {

    private final SurveyService surveyService;
    private final UserService userService;
    private final RespondRepository respondRepository;
    private final ObjAnswerRepository objAnswerRepository;
    private final SubjAnswerRepository subjAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ObjAnswerService objAnswerService;
    private final SubjAnswerService subjAnswerService;


    @Transactional
    public void saveSurveyResponses(SurveyResponseDTO surveyResponseDTO, String userId) {
        User respondedUser = userService.findByUserId(userId);
        Survey respondedSurvey = surveyService.findBySurveyId(surveyResponseDTO.getSurveyId());
        surveyService.checkSurveyExpiration(respondedSurvey.getExpireDate());
        respondRepository.save(new Respond(respondedUser, respondedSurvey));

        List<ResponseDTO> responseList = surveyResponseDTO.getResponseList();
        List<ObjAnswer> objAnswerList = objAnswerService.createObjectiveAnswers(responseList, respondedUser);
        List<SubjAnswer> subjAnswerList = subjAnswerService.createSubjectiveAnswers(responseList, respondedUser);

        subjAnswerRepository.saveAll(subjAnswerList);
        objAnswerRepository.saveAll(objAnswerList);
    }


    public SurveyResultDTO getSurveyResponse(Long surveyId, String userId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        surveyService.verifyUserMadeSurvey(userId, survey);
        List<mcnc.survwey.domain.question.dto.ResponseDTO> responseDTOList = questionRepository.findQuestionsAndAnswersBySurveyId(surveyId)
                .stream().map(mcnc.survwey.domain.question.dto.ResponseDTO::new).toList();

        SurveyResultDTO surveyResultDTO = SurveyResultDTO.of(survey);
        setAgeAndGenderCount(surveyId, surveyResultDTO);

        Map<Long, QuestionResultDTO> questionMap = new LinkedHashMap<>();

        for (mcnc.survwey.domain.question.dto.ResponseDTO responseDTO : responseDTOList) {
            updateQuestionResult(responseDTO, questionMap, surveyResultDTO);
            addResponsesToQuestion(responseDTO, questionMap.get(responseDTO.getQuesId()));
        }
        return surveyResultDTO;
    }

    private void updateQuestionResult(mcnc.survwey.domain.question.dto.ResponseDTO responseDTO, Map<Long, QuestionResultDTO> questionMap, SurveyResultDTO surveyResultDTO) {
        Long quesId = responseDTO.getQuesId();
        if (!questionMap.containsKey(quesId)) {
            QuestionResultDTO questionResult = new QuestionResultDTO(responseDTO);
            questionMap.put(quesId, questionResult);
            surveyResultDTO.getQuestionList().add(questionResult);
        }
    }

    private void addResponsesToQuestion(mcnc.survwey.domain.question.dto.ResponseDTO responseDTO, QuestionResultDTO question) {
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
