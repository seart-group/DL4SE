package usi.si.seart.analyzer.query;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Query;
import usi.si.seart.treesitter.QueryCapture;
import usi.si.seart.treesitter.QueryCursor;
import usi.si.seart.treesitter.QueryMatch;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SingleCaptureQueries implements Queries<List<Node>>, Validated {

    private final Language language;

    @Override
    public List<Node> getNodes(Node node) {
        return execute(node, "(_) @node");
    }

    protected List<Node> execute(Node node, String sExpr) {
        validate(sExpr, "Queries must contain at least one capture!");
        @Cleanup Query query = new Query(language, sExpr);
        @Cleanup QueryCursor cursor = new QueryCursor(node, query);
        Stream<QueryMatch> matches = StreamSupport.stream(cursor.spliterator(), false);
        return matches.map(QueryMatch::getCaptures)
                .flatMap(Arrays::stream)
                .map(QueryCapture::getNode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPastThreshold(int count) {
        return count >= 1;
    }
}
