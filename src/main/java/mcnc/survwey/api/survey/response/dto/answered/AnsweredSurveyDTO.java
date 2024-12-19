package mcnc.survwey.api.survey.response.dto.answered;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AnsweredSurveyDTO extends SurveyDTO {
    private String creatorId;
    private List<AnsweredQuestionDTO> questionList;

    public static AnsweredSurveyDTO of(Survey survey) {
        return AnsweredSurveyDTO.builder()
                .creatorId(survey.getUser().getUserId())
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .questionList(AnsweredQuestionDTO.ofList(survey.getQuestionList()))
                .build();
    }
}
