package mcnc.survwey.domain.survey.inquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.dto.QuestionDTO;
import mcnc.survwey.domain.question.dto.QuestionResponseDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.common.dto.SurveyWithDetailDTO;
import mcnc.survwey.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SurveyWithResponseDTO extends SurveyDTO {
    private String creatorId;
    private List<QuestionResponseDTO> questionList;

//    public static SurveyWithResponseDTO of(Survey survey) {
//        List<QuestionResponseDTO> questionDTOList = survey.getQuestionList()
//                .stream().map(QuestionResponseDTO::of).toList();
//
//        return SurveyWithResponseDTO.builder()
//                .creatorId(survey.getUser().getUserId())
//                .surveyId(survey.getSurveyId())
//                .title(survey.getTitle())
//                .description(survey.getDescription())
//                .createDate(survey.getCreateDate())
//                .expireDate(survey.getExpireDate())
//                .questionList(questionDTOList)
//                .build();
//    }
//
//    public Survey toEntity(User creator) {
//        return Survey.builder()
//                .title(this.getTitle())
//                .expireDate(this.getExpireDate())
//                .description(this.getDescription())
//                .user(creator)
//                .createDate(LocalDateTime.now())
//                .build();
//    }
}
