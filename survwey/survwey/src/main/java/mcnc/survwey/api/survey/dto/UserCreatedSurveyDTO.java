package mcnc.survwey.api.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import mcnc.survwey.domain.survey.Survey;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class UserCreatedSurveyDTO {
    private Long surveyId;
    private String title;
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;
    private long respondCount;

    public UserCreatedSurveyDTO(Survey survey) {
        this.surveyId = survey.getSurveyId();
        this.title = survey.getTitle();
        this.description = survey.getDescription();
        this.createDate = survey.getCreateDate();
        this.expireDate = survey.getExpireDate();
    }
}
