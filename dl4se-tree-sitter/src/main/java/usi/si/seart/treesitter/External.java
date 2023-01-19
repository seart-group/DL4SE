package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(value = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
abstract class External implements AutoCloseable {

    protected final long pointer;

    public final boolean isNull() {
        return pointer == 0;
    }
}
