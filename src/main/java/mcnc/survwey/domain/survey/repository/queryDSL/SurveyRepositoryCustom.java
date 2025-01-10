package mcnc.survwey.domain.survey.repository.queryDSL;

import mcnc.survwey.api.survey.inquiry.dto.SurveyWithCountDTO;
import mcnc.survwey.api.survey.inquiry.dto.SurveyWithDateDTO;
import mcnc.survwey.domain.survey.Survey;
import mcnc.survwey.api.survey.inquiry.dto.SurveyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface SurveyRepositoryCustom {

    /**
     * 참여 가능한 설문 검색
     * @param title
     * @param userId
     * @param pageable
     * @return
     * @Author 이건희
     */
    Page<SurveyDTO> findAvailableSurveyList(String title, String userId, Pageable pageable);


    /**
     * 설문 목록 & 응답자 수 조회
     * @param userId
     * @param pageable
     * @return
     * @Author 이건희
     */
    Page<SurveyWithCountDTO> findSurveyListWithRespondCountByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * 응답한 설문 조회
     * @param userId
     * @param pageable
     * @return
     * @Author 이건희
     */
    Page<SurveyWithDateDTO> findRespondedSurveyListByUserId(@Param("userId") String userId, Pageable pageable);


    /**
     * 특정 설문 조회
     * @param surveyId
     * @return
     * @Author 이건희
     */
    Survey findSurveyWithDetail(@Param("surveyId") Long surveyId);


    /**
     * 응답한 설문 검색
     * @param title
     * @param userId
     * @param pageable
     * @return
     * @Author 이건희
     */
    Page<SurveyWithDateDTO> findRespondedSurveyListByTitleAndUserId(String title, String userId, Pageable pageable);


    /**
     * 사용자가 생성한 설문 검색
     * @param title
     * @param userId
     * @param pageable
     * @return
     * @Author 이건희
     */
    Page<SurveyWithCountDTO> findUserCreatedSurveyListByTitleAndUserId(String title, String userId, Pageable pageable);
}
