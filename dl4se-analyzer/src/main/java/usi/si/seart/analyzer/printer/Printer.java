package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

public interface Printer {
    String print(Node node);
}
