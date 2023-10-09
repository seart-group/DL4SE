package usi.si.seart.exception;

import lombok.AccessLevel;
import lombok.experimental.StandardException;

@StandardException(access = AccessLevel.PROTECTED)
public abstract class EntityNotFoundException extends RuntimeException {
}
