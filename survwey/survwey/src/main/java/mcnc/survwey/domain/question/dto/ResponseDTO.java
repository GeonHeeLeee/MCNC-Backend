package mcnc.survwey.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mcnc.survwey.domain.enums.QuestionType;

import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private Long quesId;
    private String questionBody;
    private QuestionType questionType;
    private Integer sequence;
    private Boolean isEtc;
    private String selectionBody;
    private Long responseCount;
    private String subjectiveResponse;
    private String etcAnswer;

    public ResponseDTO(Map<String, Object> map) {
        this.quesId = (Long) map.get("ques_id");
        this.questionBody = (String) map.get("question_body");
        this.questionType = QuestionType.valueOf(map.get("question_type").toString());
        this.sequence = (Integer) map.get("sequence");
        this.isEtc = (Boolean) map.get("is_etc");
        this.selectionBody = (String) map.get("selection_body");
        this.responseCount = (Long) map.get("response_count");
        this.subjectiveResponse = (String) map.get("subjective_response");
        this.etcAnswer = (String) map.get("etc_answer");
    }
}
