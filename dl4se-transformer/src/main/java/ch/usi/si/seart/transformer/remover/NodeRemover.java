package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.transformer.QueryBasedTransformer;
import ch.usi.si.seart.treesitter.Node;

public abstract class NodeRemover extends QueryBasedTransformer {

    @Override
    protected final byte[] getTargetBytes(Node target) {
        return new byte[0];
    }
}
