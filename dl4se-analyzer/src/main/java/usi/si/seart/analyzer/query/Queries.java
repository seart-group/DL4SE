package usi.si.seart.analyzer.query;

import usi.si.seart.treesitter.Node;

import java.util.Collection;

public interface Queries<C extends Collection<Node>> {
    C getNodes(Node node);
    C getComments(Node node);
    C getCallableDeclarations(Node node);
}
