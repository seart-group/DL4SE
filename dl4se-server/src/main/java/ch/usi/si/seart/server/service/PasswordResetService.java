package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface PasswordResetService extends TokenService<PasswordResetToken> {

    @Service
    class PasswordResetServiceImpl extends AbstractTokenService<PasswordResetToken> {

        @Autowired
        public PasswordResetServiceImpl(PasswordResetTokenRepository tokenRepository) {
            super(tokenRepository);
        }

        @Override
        public PasswordResetToken generate(User user) {
            PasswordResetToken token = tokenRepository.findFirstByUser(user)
                    .map(existing -> {
                        existing.setValue(randomValue());
                        return existing;
                    })
                    .orElseGet(
                            () -> PasswordResetToken.builder()
                                    .user(user)
                                    .value(randomValue())
                                    .build()
                    );
            return tokenRepository.save(token);
        }
    }
}
