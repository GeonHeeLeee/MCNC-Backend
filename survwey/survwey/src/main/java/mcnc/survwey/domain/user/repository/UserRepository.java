package mcnc.survwey.domain.user.repository;

import com.querydsl.core.Tuple;
import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.repository.quertyDSL.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}