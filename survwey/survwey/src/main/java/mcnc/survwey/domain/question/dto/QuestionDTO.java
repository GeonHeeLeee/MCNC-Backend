package mcnc.survwey.domain.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.dto.SelectionDTO;
import mcnc.survwey.domain.survey.common.Survey;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {

    private long quesId;
    @NotBlank(message = "질문 내용은 필수입니다.")
    private String body;

    private QuestionType questionType;

    private List<SelectionDTO> selectionList;

    public static QuestionDTO of(Question question) {
        List<SelectionDTO> selectionDTOList = question.getSelectionList()
                .stream().map(SelectionDTO::of).toList();

        return QuestionDTO.builder()
                .quesId(question.getQuesId())
                .body(question.getBody())
                .questionType(question.getType())
                .selectionList(selectionDTOList)
                .build();
    }

    public Question toEntity(Survey survey) {
        return Question.builder()
                .body(this.getBody())
                .type(this.getQuestionType())
                .survey(survey)
                .build();
    }
}
