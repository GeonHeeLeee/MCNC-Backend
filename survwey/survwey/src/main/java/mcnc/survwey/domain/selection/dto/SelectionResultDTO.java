package mcnc.survwey.domain.selection.dto;

import lombok.*;
import mcnc.survwey.domain.question.dto.SurveyResultMapper;

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

    public SelectionResultDTO(SurveyResultMapper surveyResultMapper) {
        this.sequence = surveyResultMapper.getSequence();
        this.body = surveyResultMapper.getSelectionBody();
        this.isEtc = surveyResultMapper.getIsEtc();
        this.responseCount = surveyResultMapper.getResponseCount().intValue();
        this.etcAnswer = new ArrayList<>();
    }
}
