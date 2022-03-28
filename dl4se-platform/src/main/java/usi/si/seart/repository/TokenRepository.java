package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.user.token.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
