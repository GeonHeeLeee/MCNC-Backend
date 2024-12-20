package mcnc.survwey.domain.subjAnswer;

import jakarta.persistence.*;
import lombok.*;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subj_answer")
public class SubjAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long subjId;

    @CreationTimestamp
    private LocalDateTime writtenDate;

    @Column(columnDefinition = "TEXT")
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quesId", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    public static SubjAnswer create(User user, String response, Question question) {
        return SubjAnswer.builder()
                .response(response)
                .user(user)
                .question(question)
                .build();
    }

}
