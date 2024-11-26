package mcnc.survwey.domain.selection.dto;

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
public class SelectionResponseDTO {

    private int sequence;
    private String body;
    @JsonProperty("isEtc")
    private boolean isEtc;

    public static SelectionResponseDTO of(Selection selection) {
        return SelectionResponseDTO.builder()
                .sequence(selection.getId().getSequence())
                .isEtc(selection.isEtc())
                .body(selection.getBody())
                .build();
    }
}
