package ch.usi.si.seart.transformer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Parser;

import java.util.function.Predicate;

public abstract class NodeTransformer implements Transformer {

    protected Predicate<Node> getNodeFilter() {
        return ignored -> true;
    }

    protected final Parser getParser() {
        return Parser.getFor(getLanguage());
    }
}
