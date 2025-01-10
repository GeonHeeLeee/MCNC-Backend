package mcnc.survwey.domain.question.repository.queryDSL;

import com.querydsl.core.Tuple;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepositoryCustom {

    /**
     * 질문과 질문에 대한 응답 조회
     * @Author 이건희
     */
    List<Tuple> findQuestionsAndAnswersBySurveyId(@Param("surveyId") Long surveyId);
}
