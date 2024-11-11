package mcnc.survwey.api.survey.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDTO {
    private long surveyId;
    private String title;
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;
}
