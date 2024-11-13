package mcnc.survwey.domain.survey.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "survey")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long surveyId;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime expireDate;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @OneToMany(mappedBy = "survey")
    @JsonIgnore
    @Builder.Default
    private List<Question> questionList = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    @JsonIgnore
    @Builder.Default
    private List<Respond> respondList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.createDate == null) {
            this.createDate = LocalDateTime.now();
        }
    }
}
