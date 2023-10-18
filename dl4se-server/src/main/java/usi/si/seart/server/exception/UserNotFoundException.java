package usi.si.seart.server.exception;

import usi.si.seart.model.user.User;

import javax.persistence.metamodel.Attribute;

public class UserNotFoundException extends EntityNotFoundException {

    public <T> UserNotFoundException(Attribute<User, T> attribute, T value) {
        super(User.class, attribute, value);
    }
}
