package mcnc.survwey.domain.subjAnswer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubjAnswer is a Querydsl query type for SubjAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubjAnswer extends EntityPathBase<SubjAnswer> {

    private static final long serialVersionUID = -676666832L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubjAnswer subjAnswer = new QSubjAnswer("subjAnswer");

    public final mcnc.survwey.domain.question.QQuestion question;

    public final StringPath response = createString("response");

    public final NumberPath<Long> subjId = createNumber("subjId", Long.class);

    public final mcnc.survwey.domain.user.QUser user;

    public final DateTimePath<java.time.LocalDateTime> writtenDate = createDateTime("writtenDate", java.time.LocalDateTime.class);

    public QSubjAnswer(String variable) {
        this(SubjAnswer.class, forVariable(variable), INITS);
    }

    public QSubjAnswer(Path<? extends SubjAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubjAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubjAnswer(PathMetadata metadata, PathInits inits) {
        this(SubjAnswer.class, metadata, inits);
    }

    public QSubjAnswer(Class<? extends SubjAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new mcnc.survwey.domain.question.QQuestion(forProperty("question"), inits.get("question")) : null;
        this.user = inits.isInitialized("user") ? new mcnc.survwey.domain.user.QUser(forProperty("user")) : null;
    }

}

