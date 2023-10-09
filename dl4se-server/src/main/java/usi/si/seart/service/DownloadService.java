package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.exception.TokenExpiredException;
import usi.si.seart.exception.TokenNotFoundException;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.token.DownloadToken;
import usi.si.seart.model.user.token.Token;
import usi.si.seart.model.user.token.Token_;
import usi.si.seart.repository.TokenRepository;

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

                        if (!token.isValid())
                            throw new TokenExpiredException(token);

                        return token;
                    }).orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
        }
    }
}
