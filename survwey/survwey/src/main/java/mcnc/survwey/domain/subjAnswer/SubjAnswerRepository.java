package mcnc.survwey.domain.subjAnswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjAnswerRepository extends JpaRepository<SubjAnswer, Long> {
}
