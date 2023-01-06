package usi.si.seart.analyzer.count;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.TreeCursor;

import java.util.concurrent.atomic.AtomicLong;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TraverseCounter implements Counter {

    AtomicLong count = new AtomicLong();

    @Override
    public Long count(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        cursor.preorderTraversal(this::nodeCallback);
        Long result = count.get();
        this.reset();
        return result;
    }

    protected abstract void nodeCallback(Node node);

    protected void reset() {
        this.count.set(0);
    }
}
