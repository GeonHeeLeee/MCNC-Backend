package mcnc.survwey.api.survey.inquiry.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.Survey;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@Setter
public class SurveyWithCountDTO extends SurveyDTO {
    private long respondCount;

    public SurveyWithCountDTO(Survey survey, long respondCount) {
        super(survey);
        this.respondCount = respondCount;
    }
}
