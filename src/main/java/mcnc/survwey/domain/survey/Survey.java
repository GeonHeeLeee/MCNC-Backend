package mcnc.survwey.domain.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.respond.Respond;
import mcnc.survwey.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
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
    @CreationTimestamp
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

    /**
     * 연관관계 편의 메서드
     * @param question
     */
    public void addQuestion(Question question) {
        questionList.add(question);
        question.setSurvey(this);
    }

    public void updateExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public void updateSurvey(String title, String description, LocalDateTime expireDate){
        this.title = title;
        this.description = description;
        this.expireDate = expireDate;
    }

}
