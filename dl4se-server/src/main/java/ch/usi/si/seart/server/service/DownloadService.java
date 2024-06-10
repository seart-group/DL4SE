package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.DownloadToken;
import ch.usi.si.seart.repository.DownloadTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface DownloadService extends TokenService<DownloadToken> {

    @Service
    class DownloadServiceImpl extends AbstractTokenService<DownloadToken> {

        @Autowired
        public DownloadServiceImpl(DownloadTokenRepository tokenRepository) {
            super(tokenRepository);
        }

        @Override
        public DownloadToken generate(User user) {
            return tokenRepository.save(
                    DownloadToken.builder()
                            .user(user)
                            .value(randomValue())
                            .build()
            );
        }
    }
}
