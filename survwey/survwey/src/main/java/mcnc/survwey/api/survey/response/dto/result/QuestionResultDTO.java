package mcnc.survwey.api.survey.response.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    private long quesId;
    private String body;
    private QuestionType questionType;
    private List<SelectionResultDTO> selectionList;
    private List<String> subjAnswerList;


    public QuestionResultDTO(SurveyResultQueryDTO surveyResultQueryDTO) {
        this.quesId = surveyResultQueryDTO.getQuesId();
        this.body = surveyResultQueryDTO.getQuestionBody();
        this.questionType = surveyResultQueryDTO.getQuestionType();
        this.selectionList = new ArrayList<>();
        this.subjAnswerList = new ArrayList<>();
    }
}
