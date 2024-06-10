package ch.usi.si.seart.transformer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Parser;
import ch.usi.si.seart.treesitter.Query;
import ch.usi.si.seart.treesitter.QueryCursor;
import ch.usi.si.seart.treesitter.QueryMatch;
import ch.usi.si.seart.treesitter.Tree;
import com.google.common.primitives.Bytes;
import lombok.Cleanup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class QueryBasedTransformer extends NodeTransformer {

    protected abstract String getPattern();

    protected abstract byte[] getTargetBytes(Node target);

    @Override
    public final String apply(String source) {
        Objects.requireNonNull(source);
        byte[] bytes = source.getBytes();
        @Cleanup Parser parser = getParser();
        @Cleanup Tree tree = parser.parse(source);
        @Cleanup Query query = Query.builder()
                .language(getLanguage())
                .pattern(getPattern())
                .build();
        @Cleanup QueryCursor cursor = tree.getRootNode().walk(query);
        Stream<QueryMatch> matches = StreamSupport.stream(cursor.spliterator(), false);
        List<Node> targets = matches.map(QueryMatch::getNodes)
                .flatMap(Collection::stream)
                .filter(getNodeFilter())
                .collect(Collectors.toList());
        Collections.reverse(targets);
        for (Node target: targets) {
            byte[] prefix = Arrays.copyOfRange(bytes, 0, target.getStartByte());
            byte[] suffix = Arrays.copyOfRange(bytes, target.getEndByte(), bytes.length);
            bytes = Bytes.concat(prefix, getTargetBytes(target), suffix);
        }
        return new String(bytes);
    }
}
