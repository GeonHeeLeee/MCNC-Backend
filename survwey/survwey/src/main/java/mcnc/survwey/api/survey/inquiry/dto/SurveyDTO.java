package mcnc.survwey.api.survey.inquiry.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.Survey;


import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDTO {
    protected Long surveyId;

    @NotBlank(message = "설문 제목은 필수입니다.")
    @Size(min = 1, max = 255, message = "설문 제목은 255자 이하입니다.")
    protected String title;

    @Size(min = 1, max = 512, message = "설문 설명은 512자 이하입니다.")
    protected String description;

    protected LocalDateTime createDate;
    @NotNull(message = "만료일 지정은 필수입니다.")
    protected LocalDateTime expireDate;

    @NotNull(message = "사용자 아이디는 필수입니다.")
    protected String creatorId;

    @AssertTrue(message = "만료일은 현재 시각보다 이후여야 합니다.")
    public boolean isExpireDateValid() {
        return expireDate.isAfter(LocalDateTime.now());
    }

    public SurveyDTO(Survey survey) {
        this.surveyId = survey.getSurveyId();
        this.title = survey.getTitle();
        this.description = survey.getDescription();
        this.createDate = survey.getCreateDate();
        this.expireDate = survey.getExpireDate();
        this.creatorId = survey.getUser().getUserId();
    }

    public static SurveyDTO of(Survey survey) {
        return new SurveyDTO(survey);
    }
}
