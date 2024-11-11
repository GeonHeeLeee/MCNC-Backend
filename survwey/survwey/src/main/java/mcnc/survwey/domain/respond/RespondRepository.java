package mcnc.survwey.domain.respond;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespondRepository extends JpaRepository<Respond, Long> {

}
