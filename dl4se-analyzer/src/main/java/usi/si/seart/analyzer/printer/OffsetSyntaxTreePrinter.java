package usi.si.seart.analyzer.printer;

import lombok.AllArgsConstructor;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Point;

@AllArgsConstructor
public class OffsetSyntaxTreePrinter implements Printer {

    private final Point offset;

    @Override
    public String print(Node node) {
        return new usi.si.seart.treesitter.SyntaxTreePrinter(node, offset).printSubtree();
    }
}
