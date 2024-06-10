package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Capture;
import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Query;
import ch.usi.si.seart.treesitter.QueryCursor;
import ch.usi.si.seart.treesitter.QueryMatch;
import ch.usi.si.seart.treesitter.Tree;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class QueryBasedExtractor implements Extractor {

    protected abstract Language getLanguage();

    protected abstract List<String> getPatterns();

    protected Node getStartingNode(Tree tree) {
        return tree.getRootNode();
    }

    private void validate(Query query) {
        Set<String> captures = query.getCaptures().stream()
                .map(Capture::getName)
                .collect(Collectors.toUnmodifiableSet());
        if (!captures.contains("target"))
            throw new IllegalStateException("Query must have a 'target' capture defined!");
        if (captures.size() > 1 && !captures.contains("additional"))
            throw new IllegalStateException("Only an 'additional' capture is supported!");
    }

    @Override
    public List<Match> extract(Tree tree) {
        @Cleanup Query query = Query.builder()
                .language(getLanguage())
                .patterns(getPatterns())
                .build();
        validate(query);
        Node node = getStartingNode(tree);
        @Cleanup QueryCursor cursor = node.walk(query);
        Stream<QueryMatch> matches = StreamSupport.stream(cursor.spliterator(), false);
        return matches.map(match -> {
            Node target = match.getNodes("target").stream().findFirst().orElseThrow();
            List<Node> additional = match.getNodes("additional").stream().collect(Collectors.toUnmodifiableList());
            return new Match(target, additional);
        }).collect(Collectors.toUnmodifiableList());
    }
}
