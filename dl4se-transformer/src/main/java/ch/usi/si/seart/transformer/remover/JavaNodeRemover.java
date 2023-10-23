package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.treesitter.Language;

public abstract class JavaNodeRemover extends NodeRemover {

    @Override
    public final Language getLanguage() {
        return Language.JAVA;
    }
}
