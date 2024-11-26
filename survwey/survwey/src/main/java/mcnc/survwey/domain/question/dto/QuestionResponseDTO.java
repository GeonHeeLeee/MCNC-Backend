package mcnc.survwey.domain.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.SelectionId;
import mcnc.survwey.domain.selection.dto.SelectionDTO;
import mcnc.survwey.domain.selection.dto.SelectionResponseDTO;
import mcnc.survwey.domain.survey.common.Survey;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    private long quesId;
    @NotBlank(message = "질문 내용은 필수입니다.")
    private String body;
    private QuestionType questionType;
    private String subjAnswer;
    private String etcAnswer;
    private List<SelectionResponseDTO> selectionList;
    public List<Integer> objAnswerList;

    public static QuestionResponseDTO of(Question question) {
        List<SelectionResponseDTO> SelectionResponseDTOList = question.getSelectionList()
                .stream().map(SelectionResponseDTO::of).toList();

        return QuestionResponseDTO.builder()
                .quesId(question.getQuesId())
                .body(question.getBody())
                .questionType(question.getType())
                .selectionList(SelectionResponseDTOList)
                .subjAnswer(null)
                .etcAnswer(null)
                .objAnswerList(new ArrayList<>())
                .build();
    }
}
