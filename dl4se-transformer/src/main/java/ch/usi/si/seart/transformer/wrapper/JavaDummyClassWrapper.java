package ch.usi.si.seart.transformer.wrapper;

import ch.usi.si.seart.treesitter.Language;

public class JavaDummyClassWrapper extends Wrapper {

    @Override
    public Language getLanguage() {
        return Language.JAVA;
    }

    @Override
    protected String getPrefix() {
        return "class _ {";
    }

    @Override
    protected String getSuffix() {
        return "}";
    }

    @Override
    protected String getDelimiter() {
        return "\n";
    }
}
