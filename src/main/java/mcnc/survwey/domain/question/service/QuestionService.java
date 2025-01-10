package mcnc.survwey.domain.question.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.manage.dto.QuestionDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    /**
     * 해당 설문에 질문 생성
     * @param questionDTO
     * @param createdSurvey
     * @return
     * @Author 이건희
     */
    public Question buildAndSaveQuestion(QuestionDTO questionDTO, Survey createdSurvey) {
        Question createdQuestion = questionDTO.toEntity(createdSurvey);
        createdSurvey.addQuestion(createdQuestion);
        questionRepository.save(createdQuestion);
        return createdQuestion;
    }

    /**
     * 질문 찾기
     * @param quesId
     * @return
     * @Author 이건희
     */
    public Question findByQuesId(Long quesId) {
        return questionRepository.findById(quesId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.QUESTION_NOT_FOUND_BY_ID));
    }

    /**
     * 질문 삭제
     * @param surveyId
     * @Author 이건희
     */
    public void deleteBySurveyId(Long surveyId) {
        questionRepository.deleteBySurvey_SurveyId(surveyId);
    }
}
