package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;

public interface TokenService<T extends Token> {

    T generate(User user);
    T refresh(String value);
    void verify(String value);
    void delete(User user);
    User getOwner(String value);
}
