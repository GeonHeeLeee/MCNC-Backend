package mcnc.survwey.domain.question.repository.queryDSL;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mcnc.survwey.domain.objAnswer.QObjAnswer.*;
import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.selection.QSelection.*;
import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.*;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> findQuestionsAndAnswersBySurveyId(Long surveyId) {
        return jpaQueryFactory.select(
                        question.quesId,
                        question.body,
                        question.type,
                        selection.id.sequence,
                        selection.isEtc,
                        selection.body,
                        objAnswer.objId.count(),
                        subjAnswer.response,
                        objAnswer.etcAnswer)
                .from(question)
                .leftJoin(selection).on(question.quesId.eq(selection.id.quesId))
                .leftJoin(objAnswer).on(selection.eq(objAnswer.selection))
                .leftJoin(subjAnswer).on(question.eq(subjAnswer.question))
                .where(question.survey.surveyId.eq(surveyId))
                .groupBy(question.quesId, selection.id.sequence, selection.body, subjAnswer.response, objAnswer.etcAnswer, selection.isEtc)
                .orderBy(question.quesId.asc(), selection.id.sequence.asc())
                .fetch();
    }
//    @Query(value = "SELECT q.ques_id, q.body AS question_body, q.type AS question_type, se.sequence, se.is_etc, " +
//            "se.body AS selection_body, COUNT(ob.obj_id) AS response_count, sa.response AS subjective_response, ob.etc_answer " +
//            "FROM question q " +
//            "LEFT JOIN selection se ON q.ques_id = se.ques_id " +
//            "LEFT JOIN obj_answer ob ON se.ques_id = ob.ques_id AND se.sequence = ob.sequence " +
//            "LEFT JOIN subj_answer sa ON q.ques_id = sa.ques_id " +
//            "WHERE q.survey_id = :surveyId " +
//            "GROUP BY q.ques_id, se.sequence, se.body, sa.response, ob.etc_answer, se.is_etc " +
//            "ORDER BY q.ques_id, se.sequence", nativeQuery = true)
}
