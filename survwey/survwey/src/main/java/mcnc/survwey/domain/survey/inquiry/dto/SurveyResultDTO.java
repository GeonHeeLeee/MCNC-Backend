package mcnc.survwey.domain.survey.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.dto.QuestionResultDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultDTO extends SurveyDTO {
    List<QuestionResultDTO> questionList;
    private String creatorId;

    public static SurveyResultDTO of(Survey survey) {
        return SurveyResultDTO.builder()
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .creatorId(survey.getUser().getUserId())
                .questionList(new ArrayList<>())
                .build();
    }
}
