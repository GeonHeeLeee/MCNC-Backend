package mcnc.survwey.domain.subjAnswer.repository;

import mcnc.survwey.domain.subjAnswer.SubjAnswer;
import mcnc.survwey.domain.subjAnswer.repository.queryDSL.SubjAnswerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjAnswerRepository extends JpaRepository<SubjAnswer, Long>, SubjAnswerRepositoryCustom {
}
