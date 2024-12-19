package mcnc.survwey.api.survey.manage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.selection.SelectionId;
import mcnc.survwey.domain.survey.Survey;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QuestionDTO {

    private Long quesId;

    @NotBlank(message = "질문 내용은 필수입니다.")
    @Size(min = 1, max = 255, message = "질문 내용은 255자 이하입니다.")
    private String body;

    private QuestionType questionType;
    private List<SelectionDTO> selectionList;

    public static QuestionDTO of(Question question) {
        return QuestionDTO.builder()
                .quesId(question.getQuesId())
                .body(question.getBody())
                .questionType(question.getType())
                .selectionList(SelectionDTO.ofList(question.getSelectionList()))
                .build();
    }

    public static List<QuestionDTO> ofList(List<Question> questionList) {
        return questionList.stream()
                .map(QuestionDTO::of).toList();
    }

    public Question toEntity(Survey survey) {
        return Question.builder()
                .body(this.getBody())
                .type(this.getQuestionType())
                .survey(survey)
                .build();
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SelectionDTO {

        private SelectionId selectionId;

        @NotBlank(message = "보기 내용은 필수입니다.")
        @Size(min = 1, max = 255, message = "보기 내용은 255자 이하입니다.")
        private String body;

        @JsonProperty("isEtc")
        private boolean isEtc;

        public static SelectionDTO of(Selection selection) {
            return SelectionDTO.builder()
                    .selectionId(selection.getId())
                    .isEtc(selection.isEtc())
                    .body(selection.getBody())
                    .build();
        }

        public static List<SelectionDTO> ofList(List<Selection> selectionList) {
            return selectionList.stream()
                    .map(SelectionDTO::of)
                    .toList();
        }

        public Selection toEntity(SelectionId selectionId, Question question) {
            return Selection.builder()
                    .id(selectionId)
                    .body(this.getBody())
                    .isEtc(this.isEtc())
                    .question(question)
                    .build();
        }
    }

}
