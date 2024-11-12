package mcnc.survwey.domain.survey;

import mcnc.survwey.api.survey.dto.SurveyDTO;
import mcnc.survwey.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    List<Survey> findByUser_UserId(String userId);

    List<Survey> findByUser_Email(String email);

    List<Survey> findByTitleContainingIgnoreCase(String title);

    @Query(value = "SELECT s.survey_id, s.title, s.description, s.create_date, s.expire_date, " +
            "COALESCE(r.respond_count, 0) AS respond_count " +
            "FROM survey s " +
            "LEFT JOIN (SELECT survey_id, COUNT(*) AS respond_count " +
            "           FROM respond " +
            "           GROUP BY survey_id) r ON s.survey_id = r.survey_id " +
            "WHERE s.user_id = :userId " +
            "ORDER BY " +
            "   CASE " +
            "       WHEN s.expire_date > NOW() THEN 0 " +
            "       ELSE 1 " +
            "   END," +
            "   ABS(TIMESTAMPDIFF(SECOND, NOW(), s.expire_date)) ASC", nativeQuery = true)
    Page<Object[]> findSurveyListWithRespondCountByUserId(@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT new mcnc.survwey.api.survey.dto.SurveyDTO(s.surveyId, s.title, s.description, s.createDate, s.expireDate) " +
            "FROM Survey s " +
            "JOIN Respond r ON s = r.survey " +
            "WHERE r.user.userId = :userId " +
            "ORDER BY r.respondDate DESC")
    Page<SurveyDTO> findRespondedSurveyByUserId(@Param("userId") String userId, Pageable pageable);



    @Query("SELECT DISTINCT s " +
            "FROM Survey s " +
            "LEFT JOIN FETCH Question q ON s = q.survey " +
            "LEFT JOIN FETCH Selection se ON q = se.question " +
            "WHERE s.surveyId = :surveyId")
    Survey getSurveyWithDetail(@Param("surveyId") Long surveyId);

    Optional<Survey> findBySurveyId(Long surveyId);

    Optional<Survey> findBySurveyIdAndUser_UserId(Long surveyId, String userId);
}
