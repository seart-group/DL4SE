package ch.usi.si.seart.analyzer.enumerator;

import ch.usi.si.seart.treesitter.Node;

public interface Enumerator<E extends Enum<E>> {

    E asEnum(Node node);
}
