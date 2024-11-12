package mcnc.survwey.api.survey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;


import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {

    private long quesId;
    @NotBlank(message = "질문 내용은 필수입니다.")
    private String body;

    private QuestionType type;

    private List<SelectionDTO> selectionList;

    public static QuestionDTO of(Question question) {
        List<SelectionDTO> selectionDTOList = question.getSelectionList()
                .stream().map(SelectionDTO::of).toList();

        return QuestionDTO.builder()
                .quesId(question.getQuesId())
                .body(question.getBody())
                .type(question.getType())
                .selectionList(selectionDTOList)
                .build();

    }
}
