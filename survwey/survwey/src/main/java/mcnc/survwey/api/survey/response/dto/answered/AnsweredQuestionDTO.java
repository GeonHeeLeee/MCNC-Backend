package mcnc.survwey.api.survey.response.dto.answered;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AnsweredQuestionDTO {
    private long quesId;
    private String body;
    private QuestionType questionType;
    private String subjAnswer;
    private String etcAnswer;
    private List<AnsweredSelectionDTO> selectionList;
    public List<Integer> objAnswerList;

    public static AnsweredQuestionDTO of(Question question) {
        return AnsweredQuestionDTO.builder()
                .quesId(question.getQuesId())
                .body(question.getBody())
                .questionType(question.getType())
                .selectionList(AnsweredSelectionDTO.ofList(question.getSelectionList()))
                .subjAnswer(null)
                .etcAnswer(null)
                .objAnswerList(new ArrayList<>())
                .build();
    }

    public static List<AnsweredQuestionDTO> ofList(List<Question> questionList) {
        return questionList.stream()
                .map(AnsweredQuestionDTO::of)
                .toList();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnsweredSelectionDTO {

        private int sequence;
        private String body;
        @JsonProperty("isEtc")
        private boolean isEtc;

        public static AnsweredSelectionDTO of(Selection selection) {
            return AnsweredSelectionDTO.builder()
                    .sequence(selection.getId().getSequence())
                    .isEtc(selection.isEtc())
                    .body(selection.getBody())
                    .build();
        }

        public static List<AnsweredSelectionDTO> ofList(List<Selection> selectionList) {
            return selectionList.stream()
                    .map(AnsweredSelectionDTO::of)
                    .toList();
        }
    }

}
