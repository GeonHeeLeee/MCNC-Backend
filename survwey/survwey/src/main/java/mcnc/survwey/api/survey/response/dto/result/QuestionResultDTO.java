package mcnc.survwey.api.survey.response.dto.result;

import lombok.*;
import lombok.experimental.SuperBuilder;
import mcnc.survwey.domain.question.enums.QuestionType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResultDTO {
    private long quesId;
    private String body;
    private QuestionType questionType;
    private List<SelectionResultDTO> selectionList;
    private List<String> subjAnswerList;


    public QuestionResultDTO(SurveyResultQueryDTO surveyResultQueryDTO) {
        this.quesId = surveyResultQueryDTO.getQuesId();
        this.body = surveyResultQueryDTO.getQuestionBody();
        this.questionType = surveyResultQueryDTO.getQuestionType();
        this.selectionList = new ArrayList<>();
        this.subjAnswerList = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectionResultDTO {
        private int sequence;
        private String body;
        private boolean isEtc;
        private int responseCount;
        private List<String> etcAnswer;

        public SelectionResultDTO(SurveyResultQueryDTO surveyResultQueryDTO) {
            this.sequence = surveyResultQueryDTO.getSequence();
            this.body = surveyResultQueryDTO.getSelectionBody();
            this.isEtc = surveyResultQueryDTO.getIsEtc();
            this.responseCount = surveyResultQueryDTO.getResponseCount().intValue();
            this.etcAnswer = new ArrayList<>();
        }
    }

}
