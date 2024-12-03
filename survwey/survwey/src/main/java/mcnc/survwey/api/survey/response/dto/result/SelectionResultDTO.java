package mcnc.survwey.api.survey.response.dto.result;

import lombok.*;

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

    public SelectionResultDTO(SurveyResultQueryDTO surveyResultQueryDTO) {
        this.sequence = surveyResultQueryDTO.getSequence();
        this.body = surveyResultQueryDTO.getSelectionBody();
        this.isEtc = surveyResultQueryDTO.getIsEtc();
        this.responseCount = surveyResultQueryDTO.getResponseCount().intValue();
        this.etcAnswer = new ArrayList<>();
    }
}
