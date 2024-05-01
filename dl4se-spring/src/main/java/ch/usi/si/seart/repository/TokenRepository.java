package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@NoRepositoryBean
public interface TokenRepository<T extends Token> extends JpaRepository<T, Long> {

    void deleteByUser(@NotNull User user);
    Optional<T> findByValue(@NotBlank String value);
    Optional<T> findFirstByUser(@NotNull User user);
}
