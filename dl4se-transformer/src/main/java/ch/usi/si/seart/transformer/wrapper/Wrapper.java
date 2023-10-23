package ch.usi.si.seart.transformer.wrapper;

import ch.usi.si.seart.transformer.Transformer;

public abstract class Wrapper implements Transformer {

    protected abstract String getPrefix();

    protected abstract String getSuffix();

    protected abstract String getDelimiter();

    @Override
    public final String apply(String source) {
        return String.join(getDelimiter(), getPrefix(), source, getSuffix());
    }
}
