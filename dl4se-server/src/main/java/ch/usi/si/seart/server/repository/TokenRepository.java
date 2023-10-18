package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByValue(String value);
    List<Token> findAllByUser(User user);
}
