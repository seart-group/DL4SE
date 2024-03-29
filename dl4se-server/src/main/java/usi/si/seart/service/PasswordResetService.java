package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import usi.si.seart.exception.TokenExpiredException;
import usi.si.seart.exception.TokenNotFoundException;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.token.PasswordResetToken;
import usi.si.seart.model.user.token.Token;
import usi.si.seart.repository.TokenRepository;
import usi.si.seart.repository.UserRepository;

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
                    }).orElseThrow(() -> new TokenNotFoundException("value", value));
        }
    }
}
