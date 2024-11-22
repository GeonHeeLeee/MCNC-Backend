package mcnc.survwey.domain.question;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuestion is a Querydsl query type for Question
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestion extends EntityPathBase<Question> {

    private static final long serialVersionUID = 598713968L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuestion question = new QQuestion("question");

    public final StringPath body = createString("body");

    public final DateTimePath<java.time.LocalDateTime> createDate = createDateTime("createDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> quesId = createNumber("quesId", Long.class);

    public final ListPath<mcnc.survwey.domain.selection.Selection, mcnc.survwey.domain.selection.QSelection> selectionList = this.<mcnc.survwey.domain.selection.Selection, mcnc.survwey.domain.selection.QSelection>createList("selectionList", mcnc.survwey.domain.selection.Selection.class, mcnc.survwey.domain.selection.QSelection.class, PathInits.DIRECT2);

    public final mcnc.survwey.domain.survey.common.QSurvey survey;

    public final EnumPath<mcnc.survwey.domain.enums.QuestionType> type = createEnum("type", mcnc.survwey.domain.enums.QuestionType.class);

    public QQuestion(String variable) {
        this(Question.class, forVariable(variable), INITS);
    }

    public QQuestion(Path<? extends Question> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuestion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuestion(PathMetadata metadata, PathInits inits) {
        this(Question.class, metadata, inits);
    }

    public QQuestion(Class<? extends Question> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.survey = inits.isInitialized("survey") ? new mcnc.survwey.domain.survey.common.QSurvey(forProperty("survey"), inits.get("survey")) : null;
    }

}

