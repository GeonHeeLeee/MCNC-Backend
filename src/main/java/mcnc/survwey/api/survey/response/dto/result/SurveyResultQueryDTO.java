package mcnc.survwey.api.survey.response.dto.result;

import com.querydsl.core.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mcnc.survwey.domain.question.enums.QuestionType;

import static mcnc.survwey.domain.objAnswer.QObjAnswer.*;
import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.selection.QSelection.*;
import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultQueryDTO {
    private Long quesId;
    private String questionBody;
    private QuestionType questionType;
    private Integer sequence;
    private Boolean isEtc;
    private String selectionBody;
    private Long responseCount;
    private String subjectiveResponse;
    private String etcAnswer;

    public SurveyResultQueryDTO(Tuple tuple) {
        this.quesId = tuple.get(question.quesId);
        this.questionBody = tuple.get(question.body);
        this.questionType = tuple.get(question.type);
        this.sequence = tuple.get(selection.id.sequence);
        this.isEtc = tuple.get(selection.isEtc);
        this.selectionBody = tuple.get(selection.body);
        this.responseCount = tuple.get(objAnswer.objId.count());
        this.subjectiveResponse = tuple.get(subjAnswer.response);
        this.etcAnswer = tuple.get(objAnswer.etcAnswer);
    }
}
