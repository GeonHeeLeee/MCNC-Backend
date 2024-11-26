package mcnc.survwey.domain.respond;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRespond is a Querydsl query type for Respond
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRespond extends EntityPathBase<Respond> {

    private static final long serialVersionUID = -43482626L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRespond respond = new QRespond("respond");

    public final DateTimePath<java.time.LocalDateTime> respondDate = createDateTime("respondDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> respondId = createNumber("respondId", Long.class);

    public final mcnc.survwey.domain.survey.common.QSurvey survey;

    public final mcnc.survwey.domain.user.QUser user;

    public QRespond(String variable) {
        this(Respond.class, forVariable(variable), INITS);
    }

    public QRespond(Path<? extends Respond> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRespond(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRespond(PathMetadata metadata, PathInits inits) {
        this(Respond.class, metadata, inits);
    }

    public QRespond(Class<? extends Respond> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.survey = inits.isInitialized("survey") ? new mcnc.survwey.domain.survey.common.QSurvey(forProperty("survey"), inits.get("survey")) : null;
        this.user = inits.isInitialized("user") ? new mcnc.survwey.domain.user.QUser(forProperty("user")) : null;
    }

}

