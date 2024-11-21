package mcnc.survwey.domain.question.dto;

import com.querydsl.core.Tuple;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.objAnswer.QObjAnswer;
import mcnc.survwey.domain.question.QQuestion;
import mcnc.survwey.domain.selection.QSelection;
import mcnc.survwey.domain.subjAnswer.QSubjAnswer;

import java.util.List;
import java.util.Map;

import static mcnc.survwey.domain.objAnswer.QObjAnswer.*;
import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.selection.QSelection.*;
import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultMapper {
    private Long quesId;
    private String questionBody;
    private QuestionType questionType;
    private Integer sequence;
    private Boolean isEtc;
    private String selectionBody;
    private Long responseCount;
    private String subjectiveResponse;
    private String etcAnswer;

//    public SurveyResultMapper(Map<String, Object> map) {
//        this.quesId = (Long) map.get("ques_id");
//        this.questionBody = (String) map.get("question_body");
//        this.questionType = QuestionType.valueOf(map.get("question_type").toString());
//        this.sequence = (Integer) map.get("sequence");
//        this.isEtc = (Boolean) map.get("is_etc");
//        this.selectionBody = (String) map.get("selection_body");
//        this.responseCount = (Long) map.get("response_count");
//        this.subjectiveResponse = (String) map.get("subjective_response");
//        this.etcAnswer = (String) map.get("etc_answer");
//    }

    public SurveyResultMapper(Tuple tuple) {
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
