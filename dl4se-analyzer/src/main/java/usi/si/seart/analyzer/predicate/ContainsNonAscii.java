package usi.si.seart.analyzer.predicate;

import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Range;

public class ContainsNonAscii extends ContentPredicate {

    public ContainsNonAscii() {
        super();
    }

    public ContainsNonAscii(byte[] bytes) {
        super(bytes);
    }

    @Override
    public Boolean test(Node node) {
        if (!isReady()) return null;
        Range range = node.getRange();
        byte[] bytes = getBytesForRange(range);
        for (int i = 0; i < bytes.length; i += 2) {
            if (bytes[i] < 0 || bytes[i + 1] != 0) {
                return true;
            }
        }
        return false;
    }
}
