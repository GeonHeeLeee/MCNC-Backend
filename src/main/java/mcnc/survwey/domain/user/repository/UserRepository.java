package mcnc.survwey.domain.user.repository;

import mcnc.survwey.domain.user.User;
import mcnc.survwey.domain.user.repository.quertyDSL.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {
    boolean existsByEmail(String email);
}