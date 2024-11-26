package mcnc.survwey.domain.survey.response.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import mcnc.survwey.domain.objAnswer.service.ObjAnswerService;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.dto.QuestionResponseDTO;
import mcnc.survwey.domain.question.dto.QuestionResultDTO;
import mcnc.survwey.domain.question.dto.SurveyResultMapper;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.respond.dto.ResponseDTO;
import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.subjAnswer.service.SubjAnswerService;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.survey.response.dto.SurveyResponseDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.response.dto.SurveyReplyDTO;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.AgeCountDTO;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.subjAnswer;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyResponseService {

    private final SurveyService surveyService;
    private final UserService userService;
    private final RespondService respondService;
    private final RespondRepository respondRepository;
    private final ObjAnswerRepository objAnswerRepository;
    private final SubjAnswerRepository subjAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ObjAnswerService objAnswerService;
    private final SubjAnswerService subjAnswerService;
    private final SurveyRepository surveyRepository;

    @Transactional
    public void saveSurveyReply(SurveyReplyDTO surveyReplyDTO, String userId) {
        User respondedUser = userService.findByUserId(userId);
        Survey respondedSurvey = surveyService.findBySurveyId(surveyReplyDTO.getSurveyId());

        if (respondService.hasUserRespondedToSurvey(surveyReplyDTO.getSurveyId(), userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.HAS_ALREADY_RESPOND_TO_SURVEY);
        }

        validateQuestionInputForSurvey(surveyReplyDTO, respondedSurvey);
        surveyService.checkSurveyExpiration(respondedSurvey.getExpireDate());

        List<ResponseDTO> responseList = surveyReplyDTO.getResponseList();
        List<ObjAnswer> objAnswerList = objAnswerService.createObjectiveAnswers(responseList, respondedUser);
        List<SubjAnswer> subjAnswerList = subjAnswerService.createSubjectiveAnswers(responseList, respondedUser);

        respondRepository.save(new Respond(respondedUser, respondedSurvey));
        subjAnswerRepository.saveAll(subjAnswerList);
        objAnswerRepository.saveAll(objAnswerList);
    }

    private void validateQuestionInputForSurvey(SurveyReplyDTO surveyReplyDTO, Survey respondedSurvey) {
        Set<Long> inputQuestionSet = respondedSurvey.getQuestionList()
                .stream().map(Question::getQuesId).collect(Collectors.toSet());

        Set<Long> existingQuestionSet = surveyReplyDTO.getResponseList()
                .stream().map(ResponseDTO::getQuesId).collect(Collectors.toSet());

        if (!inputQuestionSet.equals(existingQuestionSet)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.QUESTION_NOT_MATCH_TO_SURVEY);
        }
    }


    public SurveyResultDTO getSurveyResponsesResult(Long surveyId, String userId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        surveyService.validateUserMadeSurvey(userId, survey);

        List<SurveyResultMapper> surveyResultMapperList = questionRepository.findQuestionsAndAnswersBySurveyId(surveyId)
                .stream().map(SurveyResultMapper::new).toList();

        SurveyResultDTO surveyResultDTO = SurveyResultDTO.of(survey);
        addAgeAndGenderDistribution(surveyId, surveyResultDTO);

        Map<Long, QuestionResultDTO> questionMap = new LinkedHashMap<>();

        for (SurveyResultMapper surveyResultMapper : surveyResultMapperList) {
            updateQuestionResult(surveyResultMapper, questionMap, surveyResultDTO);
            addResponsesToQuestion(surveyResultMapper, questionMap.get(surveyResultMapper.getQuesId()));
        }
        return surveyResultDTO;
    }

    private void updateQuestionResult(SurveyResultMapper surveyResultMapper, Map<Long, QuestionResultDTO> questionMap, SurveyResultDTO surveyResultDTO) {
        Long quesId = surveyResultMapper.getQuesId();
        if (!questionMap.containsKey(quesId)) {
            QuestionResultDTO questionResult = new QuestionResultDTO(surveyResultMapper);
            questionMap.put(quesId, questionResult);
            surveyResultDTO.getQuestionList().add(questionResult);
        }
    }

    private void addResponsesToQuestion(SurveyResultMapper surveyResultMapper, QuestionResultDTO question) {
        QuestionType questionType = surveyResultMapper.getQuestionType();
        switch (questionType) {
            case OBJ_MULTI:
            case OBJ_SINGLE:
                SelectionResultDTO selection = new SelectionResultDTO(surveyResultMapper);
                if (surveyResultMapper.getIsEtc() && surveyResultMapper.getEtcAnswer() != null) {
                    selection.getEtcAnswer().add(surveyResultMapper.getEtcAnswer());
                }
                question.getSelectionList().add(selection);
                break;
            case SUBJECTIVE:
                if (surveyResultMapper.getSubjectiveResponse() != null) {
                    question.getSubjAnswerList().add(surveyResultMapper.getSubjectiveResponse());
                }
                break;
            default:
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }

    private void addAgeAndGenderDistribution(Long surveyId, SurveyResultDTO surveyResultDTO) {
        List<GenderCountDTO> genderCountDTOList = userService.getGenderCountListBySurveyId(surveyId);
        List<AgeCountDTO> ageCountDTOList = userService.getAgeGroupCountBySurveyId(surveyId);

        surveyResultDTO.setAgeCountList(ageCountDTOList);
        surveyResultDTO.setGenderCountList(genderCountDTOList);
    }


    public SurveyResponseDTO getUserRespondedSurvey(Long surveyId, String userId) {
        if (!respondService.hasUserRespondedToSurvey(surveyId, userId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.HAS_NOT_RESPOND_TO_SURVEY);
        }
        SurveyResponseDTO surveyResponseDTO = Optional.ofNullable(surveyRepository.getSurveyWithDetail(surveyId))
                .map(SurveyResponseDTO::of)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));

        Map<Object, List<ObjAnswer>> objAnswerMap = getObjAnswerMap(surveyId, userId);
        Map<Long, String> subjAnswerMap = getSubjAnswerMap(surveyId, userId);

        surveyResponseDTO.getQuestionList()
                .forEach(question -> assignAnswersToQuestion(question, objAnswerMap, subjAnswerMap));

        return surveyResponseDTO;
    }

    private void assignAnswersToQuestion(QuestionResponseDTO question, Map<Object, List<ObjAnswer>> objAnswerMap, Map<Long, String> subjAnswerMap) {
        if (!objAnswerMap.containsKey(question.getQuesId()) && !subjAnswerMap.containsKey(question.getQuesId())) {
            return;
        }
        switch (question.getQuestionType()) {
            case OBJ_MULTI:
                objAnswerMap.get(question.getQuesId()).forEach(objAnswer -> {
                    question.getObjAnswerList().add(objAnswer.getSelection().getId().getSequence());
                    setEtcAnswerIfPresent(question, objAnswer);
                });
                break;

            case OBJ_SINGLE:
                ObjAnswer objAnswer = objAnswerMap.get(question.getQuesId()).get(0);
                question.getObjAnswerList().add(objAnswer.getSelection().getId().getSequence());
                setEtcAnswerIfPresent(question, objAnswer);
                break;

            case SUBJECTIVE:
                question.setSubjAnswer(subjAnswerMap.get(question.getQuesId()));
                break;

            default:
                throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_QUESTION_TYPE);
        }
    }

    private void setEtcAnswerIfPresent(QuestionResponseDTO question, ObjAnswer objAnswer) {
        if (objAnswer.getEtcAnswer() != null && !objAnswer.getEtcAnswer().isEmpty()) {
            question.setEtcAnswer(objAnswer.getEtcAnswer());
        }
    }

    private Map<Object, List<ObjAnswer>> getObjAnswerMap(Long surveyId, String userId) {
        //QuesId를 키로 가지도록
        return objAnswerRepository.findUserRespondedAnswer(surveyId, userId)
                .stream()
                .collect(Collectors.groupingBy(
                        answer -> answer.getSelection().getQuestion().getQuesId()
                ));
    }

    private Map<Long, String> getSubjAnswerMap(Long surveyId, String userId) {
        //QuesId를 키로 가지도록
        return subjAnswerRepository.findUserRespondedAnswer(surveyId, userId)
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(subjAnswer.question.quesId),
                        tuple -> tuple.get(subjAnswer.response)
                ));
    }

}
