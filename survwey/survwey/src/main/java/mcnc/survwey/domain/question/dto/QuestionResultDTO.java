package mcnc.survwey.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.selection.dto.SelectionResultDTO;

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

    public QuestionResultDTO(Long quesId, String body, QuestionType questionType) {
        this.quesId = quesId;
        this.body = body;
        this.questionType = questionType;
        this.selectionList = new ArrayList<>();
        this.subjAnswerList = new ArrayList<>();
    }
}
