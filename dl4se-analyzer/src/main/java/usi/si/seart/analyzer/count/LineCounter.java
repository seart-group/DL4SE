package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.Range;

public class LineCounter extends AbstractCounter {

    @Override
    public Long count(Node node) {
        Range range = node.getRange();
        Point startPoint = range.getStartPoint();
        Point endPoint = range.getEndPoint();
        return (long) (endPoint.getRow() - startPoint.getRow() + 1);
    }
}
