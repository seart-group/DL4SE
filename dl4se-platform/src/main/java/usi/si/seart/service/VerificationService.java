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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

public interface VerificationService {

    VerificationToken generate(User user);
    void verify(String tokenValue);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class VerificationServiceImpl implements VerificationService {

        TokenRepository tokenRepository;
        UserRepository userRepository;

        @Override
        public VerificationToken generate(User user) {
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

                boolean valid = token.getExpires().isAfter(LocalDateTime.now(ZoneOffset.UTC));
                if (!valid) {
                    tokenRepository.delete(token);
                    userRepository.delete(user);
                    throw new IllegalStateException("Cannot verify user, token has expired!");
                }

                user.setVerified(true);
                tokenRepository.delete(token);
                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Invalid or non-existing token!");
            }
        }
    }
}
