package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public interface UserRepository extends ExtendedJpaRepository<User, Long> {

    Optional<User> findByEmail(@NotBlank String email);

    Optional<User> findByUid(@NotBlank String uid);
}
