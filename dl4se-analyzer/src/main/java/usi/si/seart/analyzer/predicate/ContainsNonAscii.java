package usi.si.seart.analyzer.predicate;

import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Range;

public class ContainsNonAscii extends ContentPredicate {

    public ContainsNonAscii(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    public Boolean test(Node node) {
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
