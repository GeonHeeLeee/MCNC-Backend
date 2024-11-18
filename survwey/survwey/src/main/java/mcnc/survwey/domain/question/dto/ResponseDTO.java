package mcnc.survwey.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mcnc.survwey.domain.enums.QuestionType;

import static mcnc.survwey.domain.enums.QuestionType.valueOf;

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

}
