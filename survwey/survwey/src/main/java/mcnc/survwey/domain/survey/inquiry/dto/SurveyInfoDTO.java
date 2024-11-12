package mcnc.survwey.domain.survey.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import mcnc.survwey.domain.survey.common.Survey;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyInfoDTO {
    private long surveyId;
    @NotBlank(message = "설문 제목은 필수입니다.")
    private String title;
    private String userName;

    public static SurveyInfoDTO of(Survey survey) {
        return SurveyInfoDTO.builder()
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .userName(survey.getUser().getName())
                .build();
    }
}
