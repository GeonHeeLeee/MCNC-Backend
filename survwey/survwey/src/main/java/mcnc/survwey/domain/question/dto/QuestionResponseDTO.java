package mcnc.survwey.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.selection.SelectionId;


import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO extends QuestionDTO {
    private String subjAnswer;
    private String etcAnswer;
    private List<SelectionId> objAnswerList;
}
