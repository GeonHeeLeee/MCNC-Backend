package mcnc.survwey.domain.subjAnswer.repository.queryDSL;

import com.querydsl.core.Tuple;

import java.util.List;

public interface SubjAnswerRepositoryCustom {
    List<Tuple> findUserRespondedAnswer(Long surveyId, String userId);
}
