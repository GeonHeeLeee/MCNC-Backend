package mcnc.survwey.domain.question.repository;

import com.querydsl.core.Tuple;
import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.repository.queryDSL.QuestionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
    void deleteBySurvey_SurveyId(Long surveyId);
}
