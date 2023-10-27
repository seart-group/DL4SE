package ch.usi.si.seart.server.service;

import ch.usi.si.seart.exception.TokenNotFoundException;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.Token_;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.server.exception.TokenExpiredException;
import ch.usi.si.seart.server.repository.TokenRepository;
import ch.usi.si.seart.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface VerificationService {

    Token generate(User user);
    Token refresh(String value);
    void verify(String value);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class VerificationServiceImpl implements VerificationService {

        TokenRepository tokenRepository;
        UserRepository userRepository;

        @Override
        public Token generate(User user) {
            VerificationToken token = VerificationToken.builder()
                    .user(user)
                    .value(UUID.randomUUID().toString())
                    .build();
            return tokenRepository.save(token);
        }

        @Override
        public void verify(String value) {
            tokenRepository.findByValue(value)
                    .filter(VerificationToken.class::isInstance)
                    .map(token -> {
                        if (!token.isValid())
                            throw new TokenExpiredException(token);

                        User user = token.getUser();
                        user.setVerified(true);
                        tokenRepository.delete(token);
                        return userRepository.save(user);
                    }).orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
        }

        @Override
        public Token refresh(String value) {
            return tokenRepository.findByValue(value).map(token -> {
                token.setValue(UUID.randomUUID().toString());
                return tokenRepository.save(token);
            }).orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
        }
    }
}
