package mcnc.survwey.domain.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mcnc.survwey.domain.question.enums.QuestionType;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.survey.Survey;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long quesId;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surveyId", nullable = false)
    private Survey survey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @OneToMany(mappedBy = "question")
    @JsonIgnore
    @Builder.Default
    private List<Selection> selectionList = new ArrayList<>();

    /**
     * 연관관계 편의 메서드
     * @param selection
     */
    public void addSelection(Selection selection) {
        selectionList.add(selection);
        selection.setQuestion(this);
    }
}
