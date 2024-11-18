package mcnc.survwey.domain.survey.inquiry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.dto.QuestionResultDTO;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.selection.SelectionId;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.common.service.SurveyService;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyResultDTO;
import mcnc.survwey.domain.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.common.repository.SurveyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static mcnc.survwey.domain.enums.QuestionType.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyInquiryService {

    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final SurveyService surveyService;

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

    public SurveyResultDTO getSurveyResponse(Long surveyId) {
        Survey survey = surveyService.findBySurveyId(surveyId);
        List<Object[]> results = questionRepository.findQuestionsAndAnswersBySurveyId(surveyId);
        SurveyResultDTO surveyResultDTO = SurveyResultDTO.of(survey);

        Map<Long, QuestionResultDTO> questionMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            Long quesId = (Long) row[0]; // q.ques_id
            String questionBody = (String) row[1]; // questionBody
            QuestionType questionType = valueOf((String) row[2]); // questionType
            Integer sequence = (Integer) row[3]; // se.sequence
            Boolean isEtc = (Boolean) row[4]; // se.is_etc
            String selectionBody = (String) row[5]; // selectionBody
            Long selectionCount = (Long) row[6]; // selectionCount
            String subjectiveResponse = (String) row[7]; // sa.response
            String etcAnswer = (String) row[8]; // ob.etc_answer

            if (!questionMap.containsKey(quesId)) {
                QuestionResultDTO questionResult = new QuestionResultDTO(quesId, questionBody, questionType);
                questionMap.put(quesId, questionResult);
                surveyResultDTO.getQuestionList().add(questionResult);
            }

            QuestionResultDTO question = questionMap.get(quesId);
            if (questionType == OBJ_MULTI || questionType == OBJ_SINGLE) {
                SelectionResultDTO selection = new SelectionResultDTO(quesId, sequence, selectionBody, isEtc, selectionCount);
                if (isEtc != null && isEtc && etcAnswer != null) {
                    selection.getEtcAnswer().add(etcAnswer);
                }
                question.getSelectionList().add(selection);
            } else if (questionType == SUBJECTIVE) {
                question.getSubjAnswerList().add(subjectiveResponse);
            }
        }
        return surveyResultDTO;
    }

}
