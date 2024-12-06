package mcnc.survwey.domain.survey.repository.queryDSL;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.respond.QRespond.*;
import static mcnc.survwey.domain.selection.QSelection.*;
import static mcnc.survwey.domain.survey.QSurvey.survey;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SurveyWithCountDTO> findSurveyListWithRespondCountByUserId(String userId, Pageable pageable) {
        List<SurveyWithCountDTO> surveyWithCountDTOList = jpaQueryFactory
                .select(
                        Projections.constructor(SurveyWithCountDTO.class,
                                survey.surveyId,
                                survey.title,
                                survey.description,
                                survey.createDate,
                                survey.expireDate,
                                survey.user.userId,
                                respond.respondId.count()))
                .from(survey)
                .leftJoin(respond).on(survey.eq(respond.survey))
                .where(survey.user.userId.eq(userId))
                .groupBy(survey.surveyId)
                .orderBy(survey.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalQuery = jpaQueryFactory
                .select(survey.count())
                .from(survey)
                .where(survey.user.userId.eq(userId));

        return PageableExecutionUtils.getPage(surveyWithCountDTOList, pageable, totalQuery::fetchOne);
    }

//            CASE
//                WHEN s.expire_date > NOW() THEN 0
//                ELSE 1
//            END,
//            ABS(TIMESTAMPDIFF(SECOND, NOW(), s.expire_date)) ASC
//        """;


    @Override
    public Page<SurveyDTO> findRespondedSurveyByUserId(String userId, Pageable pageable) {
        List<SurveyDTO> surveyDTOList = jpaQueryFactory
                .select(Projections.constructor(
                        SurveyDTO.class,
                        survey.surveyId,
                        survey.title,
                        survey.description,
                        survey.createDate,
                        survey.expireDate,
                        survey.user.userId))
                .from(survey)
                .join(respond).on(respond.survey.eq(survey))
                .where(respond.user.userId.eq(userId))
                .orderBy(respond.respondDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(survey.count())
                .from(survey)
                .join(respond).on(respond.survey.eq(survey))
                .where(respond.user.userId.eq(userId));

        return PageableExecutionUtils.getPage(surveyDTOList, pageable, countQuery::fetchOne);
    }


    @Override
    public Survey getSurveyWithDetail(Long surveyId) {
        return jpaQueryFactory.selectDistinct(survey)
                .from(survey)
                .leftJoin(question).on(question.survey.eq(survey))
                .fetchJoin() //관련된 것 한번에 가져오기 위해
                .leftJoin(selection).on(selection.question.eq(question))
                .fetchJoin()
                .where(survey.surveyId.eq(surveyId))
                .fetchFirst();
    }

}
