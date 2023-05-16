package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;

public class SyntaxTreePrinter extends AbstractPrinter {

    @Override
    public String print(Node node) {
        return new ch.usi.si.seart.treesitter.SyntaxTreePrinter(node).printSubtree();
    }
}
