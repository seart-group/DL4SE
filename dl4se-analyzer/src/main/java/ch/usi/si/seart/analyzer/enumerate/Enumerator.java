package ch.usi.si.seart.analyzer.enumerate;

import ch.usi.si.seart.treesitter.Node;

public interface Enumerator<E extends Enum<E>> {

    E asEnum(Node node);
}
