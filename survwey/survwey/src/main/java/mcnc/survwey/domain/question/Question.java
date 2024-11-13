package mcnc.survwey.domain.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mcnc.survwey.domain.enums.QuestionType;
import mcnc.survwey.domain.selection.Selection;
import mcnc.survwey.domain.survey.common.Survey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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
    private LocalDateTime createDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

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

    @PrePersist
    protected void onCreate() {
        if (this.createDate == null) {
            this.createDate = LocalDateTime.now();
        }
    }
}
