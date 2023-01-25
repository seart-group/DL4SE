package usi.si.seart.analyzer.query.multi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import usi.si.seart.analyzer.query.Queries;
import usi.si.seart.analyzer.util.Tuple;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Query;
import usi.si.seart.treesitter.QueryCapture;
import usi.si.seart.treesitter.QueryCursor;
import usi.si.seart.treesitter.QueryMatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MultiCaptureQueries implements Queries<List<List<Tuple<String, Node>>>> {

    private final Language language;

    public abstract List<List<Tuple<String, Node>>> getCallableDeclarations(Node node);

    public List<List<Tuple<String, Node>>> execute(Node node, String pattern) {
        @Cleanup Query query = new Query(language, pattern);
        verify(query);
        @Cleanup QueryCursor cursor = new QueryCursor(node, query);
        Stream<QueryMatch> matches = StreamSupport.stream(cursor.spliterator(), false);
        return matches.map(match -> {
            QueryCapture[] captures = match.getCaptures();
            List<Tuple<String, Node>> tuples = new ArrayList<>(captures.length);
            for (QueryCapture capture: captures) {
                Node capturedNode = capture.getNode();
                String captureName = query.getCaptureName(capture);
                Tuple<String, Node> tuple = Tuple.of(captureName, capturedNode);
                tuples.add(tuple);
            }
            return tuples;
        }).collect(Collectors.toList());
    }
}
