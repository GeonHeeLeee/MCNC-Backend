package mcnc.survwey.domain.survey.repository.queryDSL;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithDateDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static mcnc.survwey.domain.question.QQuestion.*;
import static mcnc.survwey.domain.respond.QRespond.*;
import static mcnc.survwey.domain.selection.QSelection.*;
import static mcnc.survwey.domain.survey.QSurvey.survey;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SurveyDTO> findAvailableSurvey(String title, String userId, Pageable pageable) {
        List<SurveyDTO> surveyDTOList = jpaQueryFactory
                .select(Projections.constructor(SurveyDTO.class, survey))
                .from(survey)
                .leftJoin(respond).on(survey.eq(respond.survey)
                        .and(respond.user.userId.eq(userId)))
                .where(respond.survey.isNull()
                        .and(survey.user.userId.ne(userId))
                        .and(survey.expireDate.after(LocalDateTime.now()))
                        .and(survey.title.containsIgnoreCase(title)))
                .orderBy(survey.expireDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalQuery = jpaQueryFactory
                .select(survey.count())
                .from(survey)
                .leftJoin(respond).on(survey.eq(respond.survey)
                        .and(respond.user.userId.eq(userId)))
                .where(survey.expireDate.after(LocalDateTime.now())
                        .and(survey.title.containsIgnoreCase(title))
                        .and(survey.user.userId.ne(userId))
                        .and(respond.survey.isNull()));

        return PageableExecutionUtils.getPage(surveyDTOList, pageable, totalQuery::fetchOne);
    }

    @Override
    public Page<SurveyWithCountDTO> findSurveyListWithRespondCountByUserId(String userId, Pageable pageable) {
        List<SurveyWithCountDTO> surveyWithCountDTOList = jpaQueryFactory
                .select(Projections.constructor(SurveyWithCountDTO.class,
                        survey,
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

    @Override
    public Page<SurveyWithDateDTO> findRespondedSurveyByUserId(String userId, Pageable pageable) {
        List<SurveyWithDateDTO> surveyWithDateDTOList = jpaQueryFactory
                .select(Projections.constructor(SurveyWithDateDTO.class,
                        survey,
                        respond.respondDate))
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

        return PageableExecutionUtils.getPage(surveyWithDateDTOList, pageable, countQuery::fetchOne);
    }


    @Override
    public Survey findSurveyWithDetail(Long surveyId) {
        return jpaQueryFactory.selectDistinct(survey)
                .from(survey)
                .leftJoin(question).on(question.survey.eq(survey))
                .fetchJoin() //관련된 것 한번에 가져오기 위해
                .leftJoin(selection).on(selection.question.eq(question))
                .fetchJoin()
                .where(survey.surveyId.eq(surveyId))
                .fetchFirst();
    }

    @Override
    public Page<SurveyWithDateDTO> findRespondedSurveyByTitleAndUserId(String title, String userId, Pageable pageable) {
        List<SurveyWithDateDTO> surveyWithDateDTOList = jpaQueryFactory
                .select(Projections.constructor(SurveyWithDateDTO.class,
                        survey,
                        respond.respondDate))
                .from(survey)
                .join(respond).on(respond.survey.eq(survey))
                .where(respond.user.userId.eq(userId)
                        .and(survey.title.containsIgnoreCase(title)))
                .orderBy(respond.respondDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(survey.count())
                .from(survey)
                .join(respond).on(respond.survey.eq(survey))
                .where(respond.user.userId.eq(userId)
                        .and(survey.title.containsIgnoreCase(title)));

        return PageableExecutionUtils.getPage(surveyWithDateDTOList, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<SurveyWithCountDTO> findUserCreatedSurveyByTitleAndUserId(String title, String userId, Pageable pageable) {
        List<SurveyWithCountDTO> surveyWithCountDTOList = jpaQueryFactory
                .select(Projections.constructor(SurveyWithCountDTO.class,
                        survey,
                        respond.respondId.count()))
                .from(survey)
                .where(survey.user.userId.eq(userId)
                        .and(survey.title.containsIgnoreCase(title)))
                .orderBy(survey.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(survey.count())
                .from(survey)
                .where(survey.user.userId.eq(userId)
                        .and(survey.title.containsIgnoreCase(title)));

        return PageableExecutionUtils.getPage(surveyWithCountDTOList, pageable, countQuery::fetchOne);
    }

}
