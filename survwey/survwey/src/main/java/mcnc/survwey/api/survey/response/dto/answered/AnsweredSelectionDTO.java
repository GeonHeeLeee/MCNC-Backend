package mcnc.survwey.api.survey.response.dto.answered;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.selection.Selection;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnsweredSelectionDTO {

    private int sequence;
    private String body;
    @JsonProperty("isEtc")
    private boolean isEtc;

    public static AnsweredSelectionDTO of(Selection selection) {
        return AnsweredSelectionDTO.builder()
                .sequence(selection.getId().getSequence())
                .isEtc(selection.isEtc())
                .body(selection.getBody())
                .build();
    }
}
