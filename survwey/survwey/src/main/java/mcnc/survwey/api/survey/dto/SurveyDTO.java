package mcnc.survwey.api.survey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "설문 제목은 필수입니다.")
    private String title;
    private String description;
    private LocalDateTime createDate;
    @NotNull(message = "만료일 지정은 필수입니다.")
    private LocalDateTime expireDate;
}
