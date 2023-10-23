package ch.usi.si.seart.transformer;

import ch.usi.si.seart.treesitter.LibraryLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class BaseTest {

    static {
        LibraryLoader.load();
    }

    @Test
    final void testApply() {
        Transformer remover = getTestSubject();
        String input = getTestInput();
        String actual = remover.apply(input);
        String expected = getExpectedOutput();
        Assertions.assertEquals(expected, actual);
    }

    protected abstract Transformer getTestSubject();

    protected abstract String getTestInput();

    protected abstract String getExpectedOutput();
}
