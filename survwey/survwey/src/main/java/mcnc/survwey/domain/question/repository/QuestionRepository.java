package mcnc.survwey.domain.question.repository;

import mcnc.survwey.domain.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
