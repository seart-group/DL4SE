package ch.usi.si.seart.analyzer.extract;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Alternations">
 *     Query Syntax - Alternations
 * </a>
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Anchors">
 *     Query Syntax - Anchors
 * </a>
 */
public class JavaCallableDeclarationExtractor extends JavaExtractor {

    private static String substitute(String type) {
        return String.format("([(line_comment)(block_comment)]* @additional . (%s) @target)", type);
    }

    @Override
    protected Collection<String> getPatterns() {
        return Stream.of("method_declaration", "constructor_declaration")
                .map(JavaCallableDeclarationExtractor::substitute)
                .collect(Collectors.toUnmodifiableList());
    }
}
