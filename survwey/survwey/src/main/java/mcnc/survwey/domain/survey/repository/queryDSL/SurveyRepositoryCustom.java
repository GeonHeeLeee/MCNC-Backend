package mcnc.survwey.domain.survey.repository.queryDSL;

import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface SurveyRepositoryCustom {
    Page<SurveyDTO> findAvailableSurvey(String title, String userId, Pageable pageable);

    Page<SurveyWithCountDTO> findSurveyListWithRespondCountByUserId(@Param("userId") String userId, Pageable pageable);

    Page<SurveyDTO> findRespondedSurveyByUserId(@Param("userId") String userId, Pageable pageable);

    Survey findSurveyWithDetail(@Param("surveyId") Long surveyId);

}
