package ch.usi.si.seart.analyzer.query;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Query;

import java.util.List;

public interface Queries<C extends List<?>> {

    default void verify(Query query) {
        if (!query.hasCaptures()) {
            throw new IllegalArgumentException(
                    "Queries must contain at least one capture!"
            );
        }
    }

    C execute(Node node, String pattern);
}
