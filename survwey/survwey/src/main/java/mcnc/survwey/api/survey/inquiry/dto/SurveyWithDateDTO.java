package mcnc.survwey.api.survey.inquiry.dto;

import lombok.Getter;
import lombok.Setter;
import mcnc.survwey.domain.survey.Survey;

import java.time.LocalDateTime;

@Getter
@Setter
public class SurveyWithDateDTO extends SurveyDTO {
    private LocalDateTime respondDate;

    public SurveyWithDateDTO(Survey survey, LocalDateTime respondDate) {
        super(survey);
        this.respondDate = respondDate;
    }
}
