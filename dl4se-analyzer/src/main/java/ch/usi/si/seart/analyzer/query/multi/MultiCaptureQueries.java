package ch.usi.si.seart.analyzer.query.multi;

import ch.usi.si.seart.analyzer.query.Queries;
import ch.usi.si.seart.treesitter.Capture;
import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Query;
import ch.usi.si.seart.treesitter.QueryCursor;
import ch.usi.si.seart.treesitter.QueryMatch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MultiCaptureQueries implements Queries<List<List<Pair<String, Node>>>> {

    private final Language language;

    public abstract List<List<Pair<String, Node>>> getCallableDeclarations(Node node);

    public List<List<Pair<String, Node>>> execute(Node node, String pattern) {
        @Cleanup Query query = Query.getFor(language, pattern);
        verify(query);
        @Cleanup QueryCursor cursor = node.walk(query);
        Stream<QueryMatch> matches = StreamSupport.stream(cursor.spliterator(), false);
        return matches.map(match -> {
            Map<Capture, Collection<Node>> captures = match.getCaptures();
            List<Pair<String, Node>> tuples = new ArrayList<>(captures.size());
            for (Map.Entry<Capture, Collection<Node>> entries: captures.entrySet()) {
                String captureName = entries.getKey().getName();
                for (Node capturedNode: entries.getValue()) {
                    Pair<String, Node> tuple = Pair.of(captureName, capturedNode);
                    tuples.add(tuple);
                }
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
                    public List<List<Pair<String, Node>>> getCallableDeclarations(Node node) {
                        return Collections.emptyList();
                    }

                    @Override
                    public List<List<Pair<String, Node>>> execute(Node node, String pattern) {
                        return Collections.emptyList();
                    }
                };
        }
    }
}
