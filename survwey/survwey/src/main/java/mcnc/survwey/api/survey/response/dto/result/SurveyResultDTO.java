package mcnc.survwey.api.survey.response.dto.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultDTO extends SurveyDTO {
    private String creatorId;
    private long responseCount;
    List<GenderCountDTO> genderCountList;
    List<AgeCountDTO> ageCountList;
    List<QuestionResultDTO> questionList;

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

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GenderCountDTO {
        private String gender;
        private long count;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AgeCountDTO {
        private String age;
        private long count;
    }
}
