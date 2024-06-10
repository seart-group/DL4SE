package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.repository.VerificationTokenRepository;
import ch.usi.si.seart.server.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface VerificationService extends TokenService<VerificationToken> {

    @Service
    class VerificationServiceImpl extends AbstractTokenService<VerificationToken> {

        @Autowired
        public VerificationServiceImpl(VerificationTokenRepository tokenRepository) {
            super(tokenRepository);
        }

        @Override
        public VerificationToken generate(User user) {
            return tokenRepository.save(
                    VerificationToken.builder()
                            .user(user)
                            .value(randomValue())
                            .build()
            );
        }

        @Override
        protected void verify(VerificationToken token) {
            // This allows refresh in case of expiry
            if (token.isExpired()) throw new TokenExpiredException(token);
            tokenRepository.delete(token);
        }
    }
}
