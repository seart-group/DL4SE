package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface TokenRepository<T extends Token> extends JpaRepository<T, Long> {

    Optional<T> findByValue(String value);

    Optional<T> findFirstByUser(User user);
}
