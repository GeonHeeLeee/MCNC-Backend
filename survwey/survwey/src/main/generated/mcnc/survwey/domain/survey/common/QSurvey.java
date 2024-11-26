package mcnc.survwey.domain.survey.common;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSurvey is a Querydsl query type for Survey
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSurvey extends EntityPathBase<Survey> {

    private static final long serialVersionUID = 560938151L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSurvey survey = new QSurvey("survey");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> expireDate = createDateTime("expireDate", java.time.LocalDateTime.class);

    public final ListPath<mcnc.survwey.domain.question.Question, mcnc.survwey.domain.question.QQuestion> questionList = this.<mcnc.survwey.domain.question.Question, mcnc.survwey.domain.question.QQuestion>createList("questionList", mcnc.survwey.domain.question.Question.class, mcnc.survwey.domain.question.QQuestion.class, PathInits.DIRECT2);

    public final ListPath<mcnc.survwey.domain.respond.Respond, mcnc.survwey.domain.respond.QRespond> respondList = this.<mcnc.survwey.domain.respond.Respond, mcnc.survwey.domain.respond.QRespond>createList("respondList", mcnc.survwey.domain.respond.Respond.class, mcnc.survwey.domain.respond.QRespond.class, PathInits.DIRECT2);

    public final NumberPath<Long> surveyId = createNumber("surveyId", Long.class);

    public final StringPath title = createString("title");

    public final mcnc.survwey.domain.user.QUser user;

    public QSurvey(String variable) {
        this(Survey.class, forVariable(variable), INITS);
    }

    public QSurvey(Path<? extends Survey> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSurvey(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSurvey(PathMetadata metadata, PathInits inits) {
        this(Survey.class, metadata, inits);
    }

    public QSurvey(Class<? extends Survey> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new mcnc.survwey.domain.user.QUser(forProperty("user")) : null;
    }

}

