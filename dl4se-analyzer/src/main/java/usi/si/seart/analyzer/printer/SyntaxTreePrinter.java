package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

public class SyntaxTreePrinter implements Printer {

    @Override
    public String print(Node node) {
        return new usi.si.seart.treesitter.SyntaxTreePrinter(node).printSubtree();
    }
}
