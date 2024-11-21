package mcnc.survwey.domain.question.repository.queryDSL;

import com.querydsl.core.Tuple;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepositoryCustom {

    List<Tuple> findQuestionsAndAnswersBySurveyId(@Param("surveyId") Long surveyId);
}
