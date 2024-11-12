package mcnc.survwey.domain.respond;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(
        name = "respond",
        uniqueConstraints = @UniqueConstraint(columnNames = {"surveyId", "userId"})
)
public class Respond {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long respondId;

    private LocalDateTime respondDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;

    @PrePersist
    protected void onCreate() {
        if (this.respondDate == null) {
            this.respondDate = LocalDateTime.now();
        }
    }

    public static Respond create(User user, Survey survey) {
        return Respond.builder()
                .user(user)
                .survey(survey)
                .build();
    }
}
