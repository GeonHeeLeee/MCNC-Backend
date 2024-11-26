package mcnc.survwey.domain.survey.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.dto.QuestionResponseDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SurveyResponseDTO extends SurveyDTO {
    private String creatorId;
    private List<QuestionResponseDTO> questionList;

    public static SurveyResponseDTO of(Survey survey) {
        List<QuestionResponseDTO> questionDTOList = survey.getQuestionList()
                .stream().map(QuestionResponseDTO::of).toList();

        return SurveyResponseDTO.builder()
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
