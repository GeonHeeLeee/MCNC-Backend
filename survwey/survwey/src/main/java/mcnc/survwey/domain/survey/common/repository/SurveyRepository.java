package mcnc.survwey.domain.survey.common.repository;

import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.repository.queryDSL.SurveyRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {
    List<Survey> findByUser_UserId(String userId);

    Page<Survey> findByUser_UserIdAndTitleContainingIgnoreCase(String userId, String title, Pageable pageable);

    @Query("SELECT s FROM Survey s JOIN s.respondList r WHERE r.user.userId = :userId AND s.title LIKE %:title%")
    Page<Survey> findSurveysUserHasRespondedTo(@Param("userId") String userId, @Param("title") String title, Pageable pageable);

    Page<Survey> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
