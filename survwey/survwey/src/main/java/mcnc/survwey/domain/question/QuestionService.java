package mcnc.survwey.domain.question;

import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.dto.QuestionDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.global.exception.custom.CustomException;
import mcnc.survwey.global.exception.custom.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question buildAndSaveQuestion(QuestionDTO questionDTO, Survey createdSurvey) {
        Question createdQuestion = Question.builder()
                .body(questionDTO.getBody())
                .type(questionDTO.getQuestionType())
                .survey(createdSurvey)
                .build();
        questionRepository.save(createdQuestion);
        return createdQuestion;
    }

    public Question findByQuesId(Long quesId) {
        return questionRepository.findById(quesId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.QUESTION_NOT_FOUND_BY_ID));
    }
}
