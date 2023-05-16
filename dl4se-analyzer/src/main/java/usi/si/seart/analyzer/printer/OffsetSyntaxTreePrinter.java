package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Point;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OffsetSyntaxTreePrinter extends AbstractPrinter {

    private final Point offset;

    @Override
    public String print(Node node) {
        return new ch.usi.si.seart.treesitter.SyntaxTreePrinter(node, offset).printSubtree();
    }
}
