package mcnc.survwey.api.survey.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.Survey;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyModifyWithDetailDTO extends SurveyModifyDTO {

    private List<QuestionDTO> questionDTOList;

    public static SurveyModifyDTO of(Survey survey) {
        List<QuestionDTO> questionDTOS = survey.getQuestionList()
                .stream().map(QuestionDTO::of).toList();

        return SurveyModifyWithDetailDTO.builder()
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .expireDate(survey.getExpireDate())
                .questionList(questionDTOS)
                .build();
    }

}

