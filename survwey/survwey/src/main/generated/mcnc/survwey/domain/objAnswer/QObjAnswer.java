package mcnc.survwey.domain.objAnswer;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QObjAnswer is a Querydsl query type for ObjAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QObjAnswer extends EntityPathBase<ObjAnswer> {

    private static final long serialVersionUID = -377732802L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QObjAnswer objAnswer = new QObjAnswer("objAnswer");

    public final StringPath etcAnswer = createString("etcAnswer");

    public final NumberPath<Long> objId = createNumber("objId", Long.class);

    public final mcnc.survwey.domain.selection.QSelection selection;

    public final mcnc.survwey.domain.user.QUser user;

    public final DateTimePath<java.time.LocalDateTime> writtenDate = createDateTime("writtenDate", java.time.LocalDateTime.class);

    public QObjAnswer(String variable) {
        this(ObjAnswer.class, forVariable(variable), INITS);
    }

    public QObjAnswer(Path<? extends ObjAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QObjAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QObjAnswer(PathMetadata metadata, PathInits inits) {
        this(ObjAnswer.class, metadata, inits);
    }

    public QObjAnswer(Class<? extends ObjAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.selection = inits.isInitialized("selection") ? new mcnc.survwey.domain.selection.QSelection(forProperty("selection"), inits.get("selection")) : null;
        this.user = inits.isInitialized("user") ? new mcnc.survwey.domain.user.QUser(forProperty("user")) : null;
    }

}

