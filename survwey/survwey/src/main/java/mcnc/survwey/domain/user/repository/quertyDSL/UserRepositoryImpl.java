package mcnc.survwey.domain.user.repository.quertyDSL;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    //나이대 분포 조회
    @Override
    public List<LocalDate> findBirthBySurveyId(Long surveyId) {


        return List.of();
    }

    //성별 분포 조회
//    @Override
//    public List<Tuple[]> findGenderCountBySurveyId(Long surveyId) {
//        return queryFactory
//                .select(
//                        user.gender, user.count()).from(user)
//                .join(respond).on(user.eq(respond.user))
//                .where(respond.survey.surveyId.eq(surveyId))
//                .groupBy(user.gender)
//                .fetch();
//    }
}
