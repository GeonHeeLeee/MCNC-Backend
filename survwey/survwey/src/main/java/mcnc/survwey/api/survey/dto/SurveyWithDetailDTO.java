package mcnc.survwey.api.survey.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.survey.Survey;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SurveyWithDetailDTO extends SurveyDTO {

    private List<QuestionDTO> questionList;

    public static SurveyWithDetailDTO of(Survey survey) {
        List<QuestionDTO> questionDTOList = survey.getQuestionList()
                .stream().map(QuestionDTO::of).toList();

        return SurveyWithDetailDTO.builder()
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .questionList(questionDTOList)
                .build();
    }
}
