package mcnc.survwey.api.survey.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.Survey;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SurveyWithDetailDTO extends SurveyDTO {

    private String creatorId;
    private List<QuestionDTO> questionList;

    public static SurveyWithDetailDTO of(Survey survey) {
        List<QuestionDTO> questionDTOList = survey.getQuestionList()
                .stream().map(QuestionDTO::of).toList();

        return SurveyWithDetailDTO.builder()
                .creatorId(survey.getUser().getUserId())
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .questionList(questionDTOList)
                .build();
    }
}

