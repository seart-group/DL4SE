package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.treesitter.Language;

public abstract class PythonNodeRemover extends NodeRemover {

    @Override
    public final Language getLanguage() {
        return Language.PYTHON;
    }
}
