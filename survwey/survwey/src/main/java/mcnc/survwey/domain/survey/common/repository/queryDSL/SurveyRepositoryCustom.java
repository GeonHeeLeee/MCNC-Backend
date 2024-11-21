package mcnc.survwey.domain.survey.common.repository.queryDSL;

import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface SurveyRepositoryCustom {
    Page<Object[]> findSurveyListWithRespondCountByUserId(@Param("userId") String userId, Pageable pageable);

    Page<SurveyDTO> findRespondedSurveyByUserId(@Param("userId") String userId, Pageable pageable);

    Survey getSurveyWithDetail(@Param("surveyId") Long surveyId);

}
