package mcnc.survwey.domain.objAnswer.repository.queryDSL;

import mcnc.survwey.domain.objAnswer.ObjAnswer;

import java.util.List;

public interface ObjAnswerRepositoryCustom {
    List<ObjAnswer> findUserRespondedAnswer(Long surveyId, String userId);
}
