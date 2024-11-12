package mcnc.survwey.api.survey.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.SelectionId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectionDTO {

    private SelectionId selectionId;

    @NotBlank(message = "보기 내용은 필수입니다.")
    private String body;

    public static SelectionDTO of(Selection selection) {
        return SelectionDTO.builder()
                .selectionId(selection.getId())
                .body(selection.getBody())
                .build();
    }

    public static Selection toEntity(SelectionDTO selectionDTO, SelectionId selectionId, Question question) {
        return Selection.builder()
                .id(selectionId)
                .body(selectionDTO.getBody())
                .question(question)
                .build();
    }
}
