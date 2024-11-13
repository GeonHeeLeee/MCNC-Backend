package mcnc.survwey.domain.survey.inquiry.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mcnc.survwey.domain.survey.common.Survey;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyInfoDTO {
    private long surveyId;
    @NotBlank(message = "설문 제목은 필수입니다.")
    private String title;
    private String description;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;

    public static SurveyInfoDTO of(Survey survey) {
        return SurveyInfoDTO.builder()
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .build();
    }
}
