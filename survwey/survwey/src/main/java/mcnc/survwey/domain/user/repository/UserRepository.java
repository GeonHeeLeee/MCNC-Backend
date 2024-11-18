package mcnc.survwey.domain.user.repository;

import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.dto.GenderCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u.gender, COUNT(*) " +
            "FROM User u " +
            "JOIN Respond r ON u.userId = r.user.userId " +
            "WHERE r.survey.surveyId = :surveyId " +
            "GROUP BY u.gender ")
    List<Object[]> findGenderCountBySurveyId(Long surveyId);

    @Query("SELECT u.birth " +
            "FROM User u " +
            "JOIN Respond r ON u.userId = r.user.userId " +
            "WHERE r.survey.surveyId = :surveyId ")
    List<LocalDate> findBirthBySurveyId(Long surveyId);
}