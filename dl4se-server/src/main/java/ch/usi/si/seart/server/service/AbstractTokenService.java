package ch.usi.si.seart.server.service;

import ch.usi.si.seart.exception.TokenNotFoundException;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.Token_;
import ch.usi.si.seart.repository.TokenRepository;
import ch.usi.si.seart.server.exception.TokenExpiredException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
abstract class AbstractTokenService<T extends Token> implements TokenService<T> {

    TokenRepository<T> tokenRepository;

    protected String randomValue() {
        return UUID.randomUUID().toString();
    }

    @Override
    public T refresh(String value) {
        return tokenRepository.findByValue(value)
                .map(token -> {
                    token.setValue(randomValue());
                    return tokenRepository.save(token);
                })
                .orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
    }

    @Override
    public void verify(String value) {
        tokenRepository.findByValue(value).ifPresentOrElse(this::verify, () -> {
            throw new TokenNotFoundException(Token_.value, value);
        });
    }

    protected void verify(T token) {
        tokenRepository.delete(token);
        if (token.isExpired()) throw new TokenExpiredException(token);
    }

    @Override
    public void delete(User user) {
        tokenRepository.deleteByUser(user);
    }

    @Override
    public User getOwner(String value) {
        return tokenRepository.findByValue(value)
                .map(Token::getUser)
                .orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
    }
}
