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

    // TODO: 16.05.23
    //  This implementation will not work for languages like Python.
    //  tree-sitter treats Python strings as non-leaf nodes that can contain interpolations.
    //  Rather than editing the grammar, which would hurt users of our API, we should alter this logic.
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
