package mcnc.survwey.domain.survey.manage.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.respond.service.RespondService;
import mcnc.survwey.domain.survey.manage.dto.ResponseDTO;
import mcnc.survwey.domain.survey.manage.dto.SurveyResponseDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.objAnswer.repository.ObjAnswerRepository;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.service.QuestionService;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.respond.repository.RespondRepository;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.service.SelectionService;
import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.SubjAnswerRepository;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.service.UserService;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyManageService {

    private final SurveyService surveyService;
    private final QuestionService questionService;
    private final SelectionService selectionService;
    private final UserService userService;
    private final RespondService respondService;
    private final RespondRepository respondRepository;
    private final ObjAnswerRepository objAnswerRepository;
    private final SubjAnswerRepository subjAnswerRepository;


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

    public boolean deleteSurvey(Long surveyId) {
        return surveyService.deleteSurveyById(surveyId);
    }

    @Transactional
    public void saveSurveyResponses(SurveyResponseDTO surveyResponseDTO, String userId) {
        User respondedUser = userService.findByUserId(userId);
        Survey respondedSurvey = surveyService.findBySurveyId(surveyResponseDTO.getSurveyId());
        if (respondedSurvey.getExpireDate().isAfter(LocalDateTime.now())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.EXPIRED_SURVEY);
        }
        Respond respond = Respond.create(respondedUser, respondedSurvey);
        respondRepository.save(respond);

        List<ResponseDTO> responseList = surveyResponseDTO.getResponseList();
        List<ObjAnswer> objAnswerList = createObjectiveAnswers(responseList, respondedUser);
        List<SubjAnswer> subjAnswerList = createSubjectiveAnswers(responseList, respondedUser);
        subjAnswerRepository.saveAll(subjAnswerList);
        objAnswerRepository.saveAll(objAnswerList);

    }

    private List<SubjAnswer> createSubjectiveAnswers(List<ResponseDTO> responseList, User respondedUser) {
        return responseList.stream()
                .filter(responseDTO -> responseDTO.getQuestionType() == QuestionType.SUBJECTIVE)
                .map(responseDTO -> {
                    Question question = questionService.findByQuesId(responseDTO.getQuesId());
                    return SubjAnswer.create(respondedUser, responseDTO.getResponse(), question);
                })
                .collect(Collectors.toList());
    }

    private List<ObjAnswer> createObjectiveAnswers(List<ResponseDTO> responseList, User respondedUser) {
        return responseList.stream()
                .filter(responseDTO -> responseDTO.getQuestionType() == QuestionType.OBJ_MULTI || responseDTO.getQuestionType() == QuestionType.OBJ_SINGLE)
                .map(responseDTO -> {
                    Selection selection = selectionService.findBySelectionId(responseDTO.getSelectionId());
                    return ObjAnswer.create(respondedUser, responseDTO.getResponse(), selection);
                })
                .collect(Collectors.toList());
    }

    /**
     * 설문 수정
     * @param surveyWithDetailDTO
     * @param userId
     * @return
     */
    @Transactional
    public SurveyWithDetailDTO surveyModifyWithDetails(SurveyWithDetailDTO surveyWithDetailDTO, String userId) {
        respondService.existsBySurveyId(surveyWithDetailDTO.getSurveyId());
        //설문 응답자가 존재하면 error

        log.info(surveyWithDetailDTO.toString());
        if (deleteSurvey(surveyWithDetailDTO.getSurveyId())) {
            Survey survey = Optional.ofNullable(saveSurveyWithDetails(surveyWithDetailDTO, userId))
                    .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND_BY_ID));
            return SurveyWithDetailDTO.of(survey);
        } else {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.SURVEY_NOT_FOUND);
        }
    }
}
