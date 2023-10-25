package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends ExtendedJpaRepository<Token, Long> {

    Optional<Token> findByValue(String value);

    List<Token> findAllByUser(User user);
}
