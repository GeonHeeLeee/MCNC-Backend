package mcnc.survwey.domain.selection;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSelectionId is a Querydsl query type for SelectionId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QSelectionId extends BeanPath<SelectionId> {

    private static final long serialVersionUID = -316149081L;

    public static final QSelectionId selectionId = new QSelectionId("selectionId");

    public final NumberPath<Long> quesId = createNumber("quesId", Long.class);

    public final NumberPath<Integer> sequence = createNumber("sequence", Integer.class);

    public QSelectionId(String variable) {
        super(SelectionId.class, forVariable(variable));
    }

    public QSelectionId(Path<? extends SelectionId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSelectionId(PathMetadata metadata) {
        super(SelectionId.class, metadata);
    }

}

