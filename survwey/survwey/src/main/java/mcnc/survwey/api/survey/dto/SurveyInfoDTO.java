package mcnc.survwey.api.survey.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyInfoDTO {
    private long surveyId;
    @NotBlank(message = "설문 제목은 필수입니다.")
    private String title;
    private String username;
}
