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

    public SelectionResultDTO(Integer sequence, String body, Boolean isEtc, Long count) {
        this.sequence = sequence;
        this.body = body;
        this.isEtc = isEtc != null && isEtc;
        this.responseCount = count != null ? count.intValue() : 0;
        this.etcAnswer = new ArrayList<>();
    }
    public SelectionResultDTO(ResponseDTO responseDTO) {
        this.sequence = responseDTO.getSequence();
        this.body = responseDTO.getSelectionBody();
        this.isEtc = responseDTO.getIsEtc();
        this.responseCount = responseDTO.getResponseCount().intValue();
        this.etcAnswer = new ArrayList<>();
    }
}
