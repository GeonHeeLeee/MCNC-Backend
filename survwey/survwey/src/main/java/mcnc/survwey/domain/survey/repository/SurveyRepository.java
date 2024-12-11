package mcnc.survwey.domain.survey.repository;

import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.domain.survey.repository.queryDSL.SurveyRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {
}
