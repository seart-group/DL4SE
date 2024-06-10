package ch.usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;

public interface Printer {

    String print(Node node);
    String print(Node... nodes);
    String print(Collection<Node> nodes);
}
