package mcnc.survwey.domain.survey.common.repository.queryDSL;


import lombok.RequiredArgsConstructor;
import mcnc.survwey.domain.survey.common.Survey;
import mcnc.survwey.domain.survey.common.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom{

    @Override
    public Page<Object[]> findSurveyListWithRespondCountByUserId(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SurveyDTO> findRespondedSurveyByUserId(String userId, Pageable pageable) {
        return null;
    }

    @Override
    public Survey getSurveyWithDetail(Long surveyId) {
        return null;
    }
}
