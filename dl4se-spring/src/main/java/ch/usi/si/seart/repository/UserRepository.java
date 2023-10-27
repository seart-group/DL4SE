package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(@NotBlank String email);

    Optional<User> findByUid(@NotBlank String uid);
}
