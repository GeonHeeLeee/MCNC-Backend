package mcnc.survwey.api.survey.inquiry.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@SuperBuilder
public class SurveyWithCountDTO extends SurveyDTO {
    private long respondCount;

    public SurveyWithCountDTO(Long surveyId, String title, String description, LocalDateTime createDate, LocalDateTime expireDate, String creatorId, long respondCount) {
        super(surveyId, title, description, createDate, expireDate, creatorId);
        this.respondCount = respondCount;
    }
}
