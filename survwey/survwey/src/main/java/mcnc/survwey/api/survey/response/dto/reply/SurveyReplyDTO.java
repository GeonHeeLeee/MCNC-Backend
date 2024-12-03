package mcnc.survwey.api.survey.response.dto.reply;


import jakarta.validation.constraints.NotNull;
import lombok.*;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.selection.SelectionId;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyReplyDTO {
    @NotNull(message = "설문 아이디는 필수입니다.")
    private Long surveyId;
    private List<ReplyDTO> responseList;
}

