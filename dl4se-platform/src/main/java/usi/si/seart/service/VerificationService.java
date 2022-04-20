package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.exception.TokenExpiredException;
import usi.si.seart.exception.TokenNotFoundException;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.token.Token;
import usi.si.seart.model.user.token.VerificationToken;
import usi.si.seart.repository.TokenRepository;
import usi.si.seart.repository.UserRepository;

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
            tokenRepository.findByValue(value).map(token -> {
                User user = token.getUser();

                if (!token.isValid())
                    throw new TokenExpiredException(token);

                user.setVerified(true);
                tokenRepository.delete(token);
                return userRepository.save(user);
            }).orElseThrow(() -> new TokenNotFoundException("value", value));
        }

        @Override
        public Token refresh(String value) {
            return tokenRepository.findByValue(value).map(token -> {
                token.setValue(UUID.randomUUID().toString());
                return tokenRepository.save(token);
            }).orElseThrow(() -> new TokenNotFoundException("value", value));
        }
    }
}
