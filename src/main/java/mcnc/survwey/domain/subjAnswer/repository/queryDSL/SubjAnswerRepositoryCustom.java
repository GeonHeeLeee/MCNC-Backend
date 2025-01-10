package mcnc.survwey.domain.subjAnswer.repository.queryDSL;

import com.querydsl.core.Tuple;

import java.util.List;

public interface SubjAnswerRepositoryCustom {
    /**
     * 사용자가 응답한 주관식 응답 조회
     * @Author 이건희
     */
    List<Tuple> findUserRespondedAnswer(Long surveyId, String userId);
}
