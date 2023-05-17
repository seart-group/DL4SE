package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.TreeCursor;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.atomic.AtomicLong;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TraverseCounter implements Counter {

    AtomicLong count = new AtomicLong();

    @Override
    public Long count(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        while (true) {
            Node current = cursor.getCurrentNode();
            nodeCallback(current);
            if (shouldExplore(current) && cursor.gotoFirstChild()) {
                continue;
            }
            while (!cursor.gotoNextSibling()) {
                if (!cursor.gotoParent()) {
                    return count.getAndSet(0);
                }
            }
        }
    }

    protected abstract void nodeCallback(Node node);

    protected boolean shouldExplore(Node node) {
        return true;
    }
}
