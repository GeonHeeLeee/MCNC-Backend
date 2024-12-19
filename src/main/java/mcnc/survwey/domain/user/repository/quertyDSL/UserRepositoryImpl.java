package mcnc.survwey.domain.user.repository.quertyDSL;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static mcnc.survwey.domain.respond.QRespond.respond;
import static mcnc.survwey.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    //나이대 분포 조회
    @Override
    public List<LocalDate> findBirthListBySurveyId(Long surveyId) {
        return queryFactory
                .select(user.birth)
                .from(user)
                .join(respond).on(user.userId.eq(respond.user.userId))
                .where(respond.survey.surveyId.eq(surveyId))
                .fetch();
    }

    //성별 분포 조회
    @Override
    public List<Tuple> findGenderCountBySurveyId(Long surveyId) {
        return queryFactory
                .select(user.gender, user.count()).from(user)
                .join(respond).on(user.eq(respond.user))
                .where(respond.survey.surveyId.eq(surveyId))
                .groupBy(user.gender)
                .fetch();
    }
}
