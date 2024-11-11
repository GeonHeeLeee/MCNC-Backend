package mcnc.survwey.domain.survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByUser_UserId(String userId);
    List<Survey> findByUser_Email(String email);
    List<Survey> findByTitleContaining(String title);

    @Query(value = "SELECT s.survey_id, s.title, s.description, s.create_date, s.expire_date, " +
                    "COALESCE(r.respond_count, 0) AS respond_count " +
                    "FROM survey s " +
                    "LEFT JOIN (SELECT survey_id, COUNT(*) AS respond_count " +
                    "           FROM respond " +
                    "           GROUP BY survey_id) r ON s.survey_id = r.survey_id " +
                    "WHERE s.user_id = :userId", nativeQuery = true)
    List<Object[]> findSurveyListWithRespondCountByUserId(@Param("userId") String userId);

}
