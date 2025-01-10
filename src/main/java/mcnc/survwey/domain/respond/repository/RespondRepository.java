package mcnc.survwey.domain.respond.repository;

import mcnc.survwey.domain.respond.Respond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespondRepository extends JpaRepository<Respond, Long> {
    /**
     * Respond 테이블에 설문이 존재하는지 확인
     * @param surveyId
     * @return
     * @Author 이강민
     */
    boolean existsBySurvey_SurveyId(Long surveyId);

    /**
     * Respond 테이블에 해당 설문 Id, 사용자 Id의 데이터가 존재하는지 확인
     * @param surveyId
     * @param userId
     * @return
     * @Author 이건희
     */
    boolean existsBySurvey_SurveyIdAndUser_UserId(Long surveyId, String userId);

    /**
     * 응답자 수 세기
     * @param surveyId
     * @return
     * @Author 이건희
     */
    long countBySurvey_SurveyId(Long surveyId);
}
