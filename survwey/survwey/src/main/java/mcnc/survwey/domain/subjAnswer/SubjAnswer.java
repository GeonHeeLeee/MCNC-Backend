package mcnc.survwey.domain.subjAnswer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subj_answer")
public class SubjAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long subjId;

    private LocalDateTime writtenDate;

    @Column(columnDefinition = "TEXT")
    private String response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quesId", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        if (this.writtenDate == null) {
            this.writtenDate = LocalDateTime.now();
        }
    }

    public static SubjAnswer create(User user, String response, Question question) {
        return SubjAnswer.builder()
                .response(response)
                .user(user)
                .question(question)
                .build();
    }

}
