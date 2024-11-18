package mcnc.survwey.domain.question.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.question.dto.QuestionDTO;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.repository.QuestionRepository;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question buildAndSaveQuestion(QuestionDTO questionDTO, Survey createdSurvey) {
        Question createdQuestion = questionDTO.toEntity(createdSurvey);
        createdSurvey.addQuestion(createdQuestion);
        questionRepository.save(createdQuestion);
        return createdQuestion;
    }

    public Question findByQuesId(Long quesId) {
        return questionRepository.findById(quesId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.QUESTION_NOT_FOUND_BY_ID));
    }
}
