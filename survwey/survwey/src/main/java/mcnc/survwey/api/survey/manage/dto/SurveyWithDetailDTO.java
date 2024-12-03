package mcnc.survwey.api.survey.manage.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;

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
                .creatorId(survey.getUser().getUserId())
                .surveyId(survey.getSurveyId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .createDate(survey.getCreateDate())
                .expireDate(survey.getExpireDate())
                .questionList(questionDTOList)
                .build();
    }

    public Survey toEntity(User creator) {
        return Survey.builder()
                .title(this.getTitle())
                .expireDate(this.getExpireDate())
                .description(this.getDescription())
                .user(creator)
                .createDate(this.getCreateDate() == null ? LocalDateTime.now() : this.getCreateDate())
                .build();
    }
}

