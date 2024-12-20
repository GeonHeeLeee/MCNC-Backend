package mcnc.survwey.domain.respond;


import jakarta.persistence.*;
import lombok.*;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
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

    @CreationTimestamp
    private LocalDateTime respondDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;

    public Respond(User user, Survey survey) {
        this.user = user;
        this.survey = survey;
    }

}
