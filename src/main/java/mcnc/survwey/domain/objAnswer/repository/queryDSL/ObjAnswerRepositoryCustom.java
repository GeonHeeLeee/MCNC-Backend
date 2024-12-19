package mcnc.survwey.domain.objAnswer.repository.queryDSL;

import mcnc.survwey.domain.objAnswer.ObjAnswer;

import java.util.List;

public interface ObjAnswerRepositoryCustom {
    //사용자가 응답한 객관식 응답 조회
    List<ObjAnswer> findUserRespondedAnswer(Long surveyId, String userId);
}
