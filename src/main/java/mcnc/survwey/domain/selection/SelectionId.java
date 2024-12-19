package mcnc.survwey.domain.selection;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SelectionId implements Serializable {
    @Column(name = "ques_id")
    private Long quesId;

    @Column(name = "sequence")
    private Integer sequence;

}