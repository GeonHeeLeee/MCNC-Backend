package mcnc.survwey.api.survey.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.Question;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class SurveyModifyDTO {

    private Long surveyId;

    @NotNull(message = "만료일 지정은 필수입니다.")
    private LocalDateTime expireDate;

    @NotBlank(message = "설문 제목은 필수입니다.")
    private String title;

    private String description;

    private List<QuestionDTO> questionList;
}
