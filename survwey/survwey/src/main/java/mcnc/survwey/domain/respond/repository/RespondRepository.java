package mcnc.survwey.domain.respond.repository;

import mcnc.survwey.domain.respond.Respond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespondRepository extends JpaRepository<Respond, Long> {
    boolean existsBySurvey_SurveyId(Long surveyId);

}
