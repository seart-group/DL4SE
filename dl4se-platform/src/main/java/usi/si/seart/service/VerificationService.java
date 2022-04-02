package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.token.Token;
import usi.si.seart.model.user.token.VerificationToken;
import usi.si.seart.repository.TokenRepository;
import usi.si.seart.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationService {

    Token generate(User user);
    Token refresh(String tokenValue);
    void verify(String tokenValue);

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
        public void verify(String tokenValue) {
            Optional<Token> existing = tokenRepository.findByValue(tokenValue);
            if (existing.isPresent()) {
                Token token = existing.get();
                User user = token.getUser();

                if (!token.isValid()) {
                    throw new IllegalStateException("Cannot verify user, token has expired!");
                }

                user.setVerified(true);
                tokenRepository.delete(token);
                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Invalid or non-existing token!");
            }
        }

        @Override
        public Token refresh(String tokenValue) {
            Optional<Token> existing = tokenRepository.findByValue(tokenValue);
            if (existing.isPresent()) {
                Token token = existing.get();
                token.setValue(UUID.randomUUID().toString());
                return tokenRepository.save(token);
            } else {
                throw new IllegalArgumentException("Invalid or non-existing token!");
            }
        }
    }
}
