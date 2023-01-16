package usi.si.seart.analyzer.enumerator;

import usi.si.seart.treesitter.Node;

public interface Enumerator<E extends Enum<E>> {
    E asEnum(Node node);
}
