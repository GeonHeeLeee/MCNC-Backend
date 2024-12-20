package mcnc.survwey.domain.objAnswer;

import jakarta.persistence.*;
import lombok.*;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "obj_answer")
public class ObjAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long objId;

    @CreationTimestamp
    private LocalDateTime writtenDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String etcAnswer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "ques_id", referencedColumnName = "ques_id"),
            @JoinColumn(name = "sequence", referencedColumnName = "sequence")
    })
    private Selection selection;

    public static ObjAnswer create(User user, String etcAnswer, Selection selection) {
        return ObjAnswer.builder()
                .user(user)
                .selection(selection)
                .etcAnswer(etcAnswer)
                .build();
    }

}

