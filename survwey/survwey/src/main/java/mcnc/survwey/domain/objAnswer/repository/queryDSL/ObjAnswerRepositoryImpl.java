package mcnc.survwey.domain.objAnswer.repository.queryDSL;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mcnc.survwey.domain.objAnswer.QObjAnswer.*;
import static mcnc.survwey.domain.question.QQuestion.*;

@Repository
@RequiredArgsConstructor
public class ObjAnswerRepositoryImpl implements ObjAnswerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ObjAnswer> findUserRespondedAnswer(Long surveyId, String userId) {
        return jpaQueryFactory
                .select(objAnswer)
                .from(objAnswer)
                .leftJoin(question).on(objAnswer.selection.question.eq(question))
                .where(question.survey.surveyId.eq(surveyId)
                        .and(objAnswer.user.userId.eq(userId)))
                .fetch();
    }
}
