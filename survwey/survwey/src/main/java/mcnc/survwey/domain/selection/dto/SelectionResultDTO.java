package mcnc.survwey.domain.selection.dto;

import lombok.*;
import mcnc.survwey.domain.question.dto.ResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectionResultDTO {
    private int sequence;
    private String body;
    private boolean isEtc;
    private int responseCount;
    private List<String> etcAnswer;

    public SelectionResultDTO(ResponseDTO responseDTO) {
        this.sequence = responseDTO.getSequence();
        this.body = responseDTO.getSelectionBody();
        this.isEtc = responseDTO.getIsEtc();
        this.responseCount = responseDTO.getResponseCount().intValue();
        this.etcAnswer = new ArrayList<>();
    }
}
