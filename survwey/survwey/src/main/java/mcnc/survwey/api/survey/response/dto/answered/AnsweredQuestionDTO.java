package mcnc.survwey.api.survey.response.dto.answered;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.question.Question;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnsweredQuestionDTO {
    private long quesId;
    @NotBlank(message = "질문 내용은 필수입니다.")
    private String body;
    private QuestionType questionType;
    private String subjAnswer;
    private String etcAnswer;
    private List<AnsweredSelectionDTO> selectionList;
    public List<Integer> objAnswerList;

    public static AnsweredQuestionDTO of(Question question) {
        List<AnsweredSelectionDTO> answeredSelectionDTOList = question.getSelectionList()
                .stream().map(AnsweredSelectionDTO::of).toList();

        return AnsweredQuestionDTO.builder()
                .quesId(question.getQuesId())
                .body(question.getBody())
                .questionType(question.getType())
                .selectionList(answeredSelectionDTOList)
                .subjAnswer(null)
                .etcAnswer(null)
                .objAnswerList(new ArrayList<>())
                .build();
    }
}
