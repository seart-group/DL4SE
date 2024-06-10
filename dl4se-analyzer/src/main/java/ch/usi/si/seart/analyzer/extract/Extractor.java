package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Tree;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Extractor {

    List<Match> extract(Tree tree);

    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    final class Match {

        Node target;

        List<Node> additional;

        public List<Node> getNodes() {
            return Stream.concat(
                    additional.stream(),
                    Stream.of(target)
            ).collect(Collectors.toUnmodifiableList());
        }
    }
}
