package mcnc.survwey.domain.selection;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSelection is a Querydsl query type for Selection
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSelection extends EntityPathBase<Selection> {

    private static final long serialVersionUID = -357870484L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSelection selection = new QSelection("selection");

    public final StringPath body = createString("body");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final QSelectionId id;

    public final BooleanPath isEtc = createBoolean("isEtc");

    public final ListPath<mcnc.survwey.domain.objAnswer.ObjAnswer, mcnc.survwey.domain.objAnswer.QObjAnswer> objAnswerList = this.<mcnc.survwey.domain.objAnswer.ObjAnswer, mcnc.survwey.domain.objAnswer.QObjAnswer>createList("objAnswerList", mcnc.survwey.domain.objAnswer.ObjAnswer.class, mcnc.survwey.domain.objAnswer.QObjAnswer.class, PathInits.DIRECT2);

    public final mcnc.survwey.domain.question.QQuestion question;

    public QSelection(String variable) {
        this(Selection.class, forVariable(variable), INITS);
    }

    public QSelection(Path<? extends Selection> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSelection(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSelection(PathMetadata metadata, PathInits inits) {
        this(Selection.class, metadata, inits);
    }

    public QSelection(Class<? extends Selection> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.id = inits.isInitialized("id") ? new QSelectionId(forProperty("id")) : null;
        this.question = inits.isInitialized("question") ? new mcnc.survwey.domain.question.QQuestion(forProperty("question"), inits.get("question")) : null;
    }

}

