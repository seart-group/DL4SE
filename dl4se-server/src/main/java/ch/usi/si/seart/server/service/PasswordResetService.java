package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.Token_;
import ch.usi.si.seart.server.exception.TokenExpiredException;
import ch.usi.si.seart.server.exception.TokenNotFoundException;
import ch.usi.si.seart.server.repository.TokenRepository;
import ch.usi.si.seart.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface PasswordResetService {

    Token generate(User user);
    void verify(String value, String newPass);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class PasswordResetServiceImpl implements PasswordResetService {

        PasswordEncoder passwordEncoder;
        TokenRepository tokenRepository;
        UserRepository userRepository;

        @Override
        public Token generate(User user) {
            String value = UUID.randomUUID().toString();
            Token token = tokenRepository.findAllByUser(user)
                    .stream()
                    .filter(PasswordResetToken.class::isInstance)
                    .findFirst()
                    .map(existing -> {
                        existing.setValue(value);
                        return existing;
                    })
                    .orElseGet(() ->
                            PasswordResetToken.builder()
                                    .user(user)
                                    .value(value)
                                    .build()
                    );
            return tokenRepository.save(token);
        }

        @Override
        public void verify(String value, String newPass) {
            tokenRepository.findByValue(value)
                    .filter(PasswordResetToken.class::isInstance)
                    .map(token -> {
                        if (!token.isValid())
                            throw new TokenExpiredException(token);

                        User user = token.getUser();
                        user.setPassword(passwordEncoder.encode(newPass));
                        tokenRepository.delete(token);
                        return userRepository.save(user);
                    }).orElseThrow(() -> new TokenNotFoundException(Token_.value, value));
        }
    }
}
