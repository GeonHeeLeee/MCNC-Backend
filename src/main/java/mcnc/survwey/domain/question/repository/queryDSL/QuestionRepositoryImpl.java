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

    //질문과 질문에 대한 응답 조회
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
                .groupBy(question.quesId, selection.id.sequence, selection.body, subjAnswer.subjId, subjAnswer.response, objAnswer.etcAnswer, selection.isEtc)
                .orderBy(question.quesId.asc(), selection.id.sequence.asc())
                .fetch();
    }

}
