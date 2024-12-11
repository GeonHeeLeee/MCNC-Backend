package mcnc.survwey.domain.subjAnswer.repository.queryDSL;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.subjAnswer.QSubjAnswer.*;

@Repository
@RequiredArgsConstructor
public class SubjAnswerRepositoryImpl implements SubjAnswerRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Tuple> findUserRespondedAnswer(Long surveyId, String userId) {
        return jpaQueryFactory
                .select(subjAnswer.response,
                        subjAnswer.question.quesId)
                .from(subjAnswer)
                .leftJoin(question).on(subjAnswer.question.eq(question))
                .where(question.survey.surveyId.eq(surveyId)
                        .and(subjAnswer.user.userId.eq(userId)))
                .fetch();
    }
}
