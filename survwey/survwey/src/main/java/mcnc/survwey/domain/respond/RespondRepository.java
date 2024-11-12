package mcnc.survwey.domain.respond;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RespondRepository extends JpaRepository<Respond, Long> {
    boolean existsBySurvey_SurveyId(Long surveyId);
}
