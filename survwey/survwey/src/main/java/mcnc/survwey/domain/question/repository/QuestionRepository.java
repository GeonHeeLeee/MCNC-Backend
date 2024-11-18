package mcnc.survwey.domain.question.repository;

import mcnc.survwey.domain.question.Question;
import mcnc.survwey.domain.question.dto.ResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT new mcnc.survwey.domain.question.dto.ResponseQueryDTO(q.ques_id, q.body AS questionBody, q.type AS questionType, se.sequence, se.is_etc, " +
            "se.body AS selectionBody, COUNT(ob.obj_id) AS responseCount, sa.response AS subjectiveResponse, ob.etc_answer) " +
            "FROM question q " +
            "LEFT JOIN selection se ON q.ques_id = se.ques_id " +
            "LEFT JOIN obj_answer ob ON se.ques_id = ob.ques_id AND se.sequence = ob.sequence " +
            "LEFT JOIN subj_answer sa ON q.ques_id = sa.ques_id " +
            "WHERE q.survey_id = :surveyId " +
            "GROUP BY q.ques_id, se.sequence, se.body, sa.response, ob.etc_answer, se.is_etc " +
            "ORDER BY q.ques_id, se.sequence", nativeQuery = true)
    List<ResponseDTO> findQuestionsAndAnswersBySurveyId(@Param("surveyId") Long surveyId);

}
