package mcnc.survwey.domain.selection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import mcnc.survwey.domain.objAnswer.ObjAnswer;
import mcnc.survwey.domain.question.Question;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "selection")
public class Selection implements Persistable<SelectionId> {

    @EmbeddedId
    private SelectionId id;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createDate;

    private boolean isEtc;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ques_id", insertable = false, updatable = false)
    private Question question;

    @OneToMany(mappedBy = "selection")
    @JsonIgnore
    @Builder.Default
    private List<ObjAnswer> objAnswerList = new ArrayList<>();

    /**
     * 복합키일 경우 insert시 select 쿼리가 나가는 문제 해결
     * - createDate가 null일 경우 새로운 Entity임
     * - createDate가 null이 아닐 경우 새로운 Entity가 아님
     * @return
     */
    @Override
    public boolean isNew() {
        return createDate == null;
    }

}
