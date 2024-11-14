package mcnc.survwey.domain.objAnswer.repository;

import mcnc.survwey.domain.objAnswer.ObjAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjAnswerRepository extends JpaRepository<ObjAnswer, Long> {
}