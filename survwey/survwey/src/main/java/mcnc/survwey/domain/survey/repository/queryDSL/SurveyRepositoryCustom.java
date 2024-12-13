package mcnc.survwey.domain.survey.repository.queryDSL;

import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithDateDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface SurveyRepositoryCustom {
    //참여 가능한 설문 검색
    Page<SurveyDTO> findAvailableSurvey(String title, String userId, Pageable pageable);

    //설문 목록 & 응답자 수 조회
    Page<SurveyWithCountDTO> findSurveyListWithRespondCountByUserId(@Param("userId") String userId, Pageable pageable);

    //응답한 설문 조회
    Page<SurveyWithDateDTO> findRespondedSurveyByUserId(@Param("userId") String userId, Pageable pageable);

    //특정 설문 조회
    Survey findSurveyWithDetail(@Param("surveyId") Long surveyId);

    //응답한 설문 검색
    Page<SurveyWithDateDTO> findRespondedSurveyByTitleAndUserId(String title, String userId, Pageable pageable);

    //생성한 설문 검색
    Page<SurveyWithCountDTO> findUserCreatedSurveyByTitleAndUserId(String title, String userId, Pageable pageable);
}
