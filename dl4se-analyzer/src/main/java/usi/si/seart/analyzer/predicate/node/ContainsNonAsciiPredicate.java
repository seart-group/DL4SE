package usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import usi.si.seart.analyzer.NodeMapper;

public class ContainsNonAsciiPredicate extends NodeContentPredicate {

    public ContainsNonAsciiPredicate(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    public boolean test(Node node) {
        Range range = node.getRange();
        byte[] bytes = mapper.getBytesForRange(range);
        for (int i = 0; i < bytes.length; i += 2) {
            if (bytes[i] < 0 || bytes[i + 1] != 0) {
                return true;
            }
        }
        return false;
    }
}
