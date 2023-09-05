package usi.si.seart.analyzer.query.multi;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Query;
import ch.usi.si.seart.treesitter.QueryCapture;
import ch.usi.si.seart.treesitter.QueryCursor;
import ch.usi.si.seart.treesitter.QueryMatch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import usi.si.seart.analyzer.query.Queries;
import usi.si.seart.analyzer.util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
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

    public static MultiCaptureQueries getInstance(Language language) {
        switch (language) {
            case JAVA:
                return new JavaMultiCaptureQueries();
            case PYTHON:
                return new PythonMultiCaptureQueries();
            default:
                return new MultiCaptureQueries(null) {

                    @Override
                    public void verify(Query query) {
                    }

                    @Override
                    public List<List<Tuple<String, Node>>> getCallableDeclarations(Node node) {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<List<Tuple<String, Node>>> execute(Node node, String pattern) {
                        return Collections.emptyList();
                    }
                };
        }
    }
}
