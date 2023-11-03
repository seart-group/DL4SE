package ch.usi.si.seart.server.service;

import ch.usi.si.seart.exception.TokenNotFoundException;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.DownloadToken;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.Token_;
import ch.usi.si.seart.repository.TokenRepository;
import ch.usi.si.seart.server.exception.TokenExpiredException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface DownloadService {

    Token generate(User user);
    void consume(String value);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class DownloadServiceImpl implements DownloadService {

        TokenRepository tokenRepository;

        @Override
        public Token generate(User user) {
            DownloadToken token = DownloadToken.builder()
                    .user(user)
                    .value(UUID.randomUUID().toString())
                    .build();
            return tokenRepository.save(token);
        }

        @Override
        public void consume(String value) {
            tokenRepository.findByValue(value)
                    .filter(DownloadToken.class::isInstance)
                    .map(token -> {
                        tokenRepository.delete(token);
                        if (token.isExpired()) throw new TokenExpiredException(token);
                        return token;
                    }).orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
        }
    }
}
