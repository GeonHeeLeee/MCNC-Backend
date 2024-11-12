package mcnc.survwey.domain.objAnswer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "obj_answer")
public class ObjAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long objId;

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

    @PrePersist
    protected void onCreate() {
        if (this.writtenDate == null) {
            this.writtenDate = LocalDateTime.now();
        }
    }

    public static ObjAnswer create(User user, String etcAnswer, Selection selection) {
        return ObjAnswer.builder()
                .user(user)
                .selection(selection)
                .etcAnswer(etcAnswer)
                .build();
    }

}

