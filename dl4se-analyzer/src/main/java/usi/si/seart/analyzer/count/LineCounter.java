package usi.si.seart.analyzer.count;

import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Point;
import usi.si.seart.treesitter.Range;

public class LineCounter implements Counter {

    @Override
    public Long count(Node node) {
        Range range = node.getRange();
        Point startPoint = range.getStartPoint();
        Point endPoint = range.getEndPoint();
        return (long) (endPoint.getRow() - startPoint.getRow() + 1);
    }
}
