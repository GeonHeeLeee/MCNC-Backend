package mcnc.survwey.domain.user.repository.quertyDSL;

import com.querydsl.core.Tuple;

import java.time.LocalDate;
import java.util.List;

public interface UserRepositoryCustom {
    /**
     * 성별 분포 조회
     * @param surveyId
     * @return
     * @Author 이건희
     */
    List<Tuple> findGenderCountBySurveyId(Long surveyId);

    /**
     * 나이대 분포 조회
     * @param surveyId
     * @return
     * @Author 이강민
     */
    List<LocalDate> findBirthListBySurveyId(Long surveyId);
}
