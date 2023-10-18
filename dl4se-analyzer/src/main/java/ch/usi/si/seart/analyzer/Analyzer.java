package ch.usi.si.seart.analyzer;

import ch.usi.si.seart.analyzer.count.CharacterCounter;
import ch.usi.si.seart.analyzer.count.CodeTokenCounter;
import ch.usi.si.seart.analyzer.count.Counter;
import ch.usi.si.seart.analyzer.count.LineCounter;
import ch.usi.si.seart.analyzer.count.TokenCounter;
import ch.usi.si.seart.analyzer.enumerator.BoilerplateEnumerator;
import ch.usi.si.seart.analyzer.enumerator.Enumerator;
import ch.usi.si.seart.analyzer.hash.ContentHasher;
import ch.usi.si.seart.analyzer.hash.Hasher;
import ch.usi.si.seart.analyzer.hash.SyntaxTreeHasher;
import ch.usi.si.seart.analyzer.io.NullFilteredReader;
import ch.usi.si.seart.analyzer.predicate.node.ContainsErrorPredicate;
import ch.usi.si.seart.analyzer.predicate.node.ContainsNonAsciiPredicate;
import ch.usi.si.seart.analyzer.predicate.node.NodePredicate;
import ch.usi.si.seart.analyzer.predicate.path.TestFilePredicate;
import ch.usi.si.seart.analyzer.printer.NodePrinter;
import ch.usi.si.seart.analyzer.printer.OffsetSyntaxTreePrinter;
import ch.usi.si.seart.analyzer.printer.Printer;
import ch.usi.si.seart.analyzer.printer.SymbolicExpressionPrinter;
import ch.usi.si.seart.analyzer.printer.SyntaxTreePrinter;
import ch.usi.si.seart.analyzer.query.multi.MultiCaptureQueries;
import ch.usi.si.seart.model.code.Boilerplate;
import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.model.code.Function;
import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Parser;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.Tree;
import com.google.common.io.CharStreams;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Analyzer implements AutoCloseable {

    Language language;
    Parser parser;
    Tree tree;

    LocalClone localClone;
    Path path;

    Counter lineCounter;
    Counter totalTokenCounter;
    Counter codeTokenCounter;
    Counter characterCounter;

    Hasher contentHasher;
    Hasher syntaxTreeHasher;

    NodePredicate containsError;
    NodePredicate containsNonAscii;

    Predicate<Path> testFilePredicate;

    Printer nodePrinter;
    Printer syntaxTreePrinter;
    Printer expressionPrinter;

    MultiCaptureQueries multiCaptureQueries;

    Enumerator<Boilerplate> boilerplateEnumerator;

    public Analyzer(LocalClone localClone, Path path) throws IOException {
        this(
                localClone,
                path,
                Language.associatedWith(path)
                        .stream()
                        .findFirst()
                        .orElseThrow()
        );
    }

    public Analyzer(LocalClone localClone, Path path, Language language) throws IOException {
        this.language = language;
        this.parser = Parser.getFor(language);
        this.localClone = localClone;
        this.path = path;
        this.tree = parser.parse(readFile(path));
        this.lineCounter = new LineCounter();
        this.totalTokenCounter = TokenCounter.getInstance(language);
        this.codeTokenCounter = CodeTokenCounter.getInstance(language);
        this.characterCounter = new CharacterCounter();
        this.contentHasher = new ContentHasher();
        this.syntaxTreeHasher = new SyntaxTreeHasher();
        this.containsError = new ContainsErrorPredicate();
        this.containsNonAscii = new ContainsNonAsciiPredicate();
        this.testFilePredicate = TestFilePredicate.getInstance(language);
        this.nodePrinter = new NodePrinter();
        this.syntaxTreePrinter = new SyntaxTreePrinter();
        this.expressionPrinter = new SymbolicExpressionPrinter();
        this.multiCaptureQueries = MultiCaptureQueries.getInstance(language);
        this.boilerplateEnumerator = BoilerplateEnumerator.getInstance(language);
    }

    @Override
    public void close() {
        tree.close();
        parser.close();
    }

    public void setParserTimeout(Duration duration) {
        parser.setTimeout(duration);
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    public static final class Result {
        File file;
        List<Function> functions;
    }

    public final Result analyze() {
        File file = extractFileEntity();
        List<Function> functions = extractFunctionEntities(file);
        file.setFunctions(functions);
        return new Result(file, functions);
    }

    private File extractFileEntity() {
        Node node = tree.getRootNode();
        return File.builder()
                .repo(localClone.getGitRepo())
                .path(localClone.relativePathOf(path).toString())
                .content(nodePrinter.print(node))
                .contentHash(contentHasher.hash(node))
                .ast(syntaxTreePrinter.print(node))
                .astHash(syntaxTreeHasher.hash(node))
                .symbolicExpression(expressionPrinter.print(node))
                .totalTokens(totalTokenCounter.count(node))
                .codeTokens(codeTokenCounter.count(node))
                .lines(lineCounter.count(node))
                .characters(characterCounter.count(node))
                .containsNonAscii(containsNonAscii.test(node))
                .containsError(containsError.test(node))
                .isTest(testFilePredicate.test(path))
                .build();
    }

    private List<Function> extractFunctionEntities(File file) {
        List<List<Pair<String, Node>>> matches = multiCaptureQueries.getCallableDeclarations(tree.getRootNode());
        List<Function> functions = new ArrayList<>(matches.size());
        for (List<Pair<String, Node>> match: matches) {
            Function function = extractFunctionEntity(match);
            // These operations are invariant by
            // nature and should not be overridden
            function.setFile(file);
            function.setIsTest(file.getIsTest());
            functions.add(function);
        }
        return functions;
    }

    private Function extractFunctionEntity(List<Pair<String, Node>> match) {
        List<Node> nodes = match.stream()
                .map(Pair::getValue)
                .collect(Collectors.toList());
        Node function = match.stream()
                .filter(tuple -> tuple.getKey().equals("target"))
                .map(Pair::getValue)
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        Printer standalone = getStandalonePrinter(language);
        String ast = standalone.print(nodes);

        return Function.builder()
                .repo(localClone.getGitRepo())
                .ast(ast)
                .content(nodePrinter.print(nodes))
                .astHash(syntaxTreeHasher.hash(nodes))
                .contentHash(contentHasher.hash(nodes))
                .symbolicExpression(expressionPrinter.print(nodes))
                .totalTokens(totalTokenCounter.count(nodes))
                .codeTokens(codeTokenCounter.count(nodes))
                .lines(lineCounter.count(nodes))
                .characters(characterCounter.count(nodes))
                .containsNonAscii(containsNonAscii.test(nodes))
                .containsError(containsError.test(nodes))
                .boilerplateType(boilerplateEnumerator.asEnum(function))
                .build();
    }

    private FunctionSyntaxTreePrinter getStandalonePrinter(Language language) {
        if (Language.JAVA.equals(language)) {
            return new FunctionSyntaxTreePrinter() {

                @Override
                protected String wrap(String content) {
                    return "class _ {\n" + content + "\n}\n";
                }

                @Override
                protected List<Node> getTargets(Tree tree) {
                    Node root = tree.getRootNode();
                    Node declaration = root.getChild(0);
                    Node body = declaration.getChildByFieldName("body");
                    return body.getChildren();
                }

                @Override
                protected Printer getAstPrinter() {
                    return new OffsetSyntaxTreePrinter(new Point(-1, 0));
                }
            };
        } else {
            return new FunctionSyntaxTreePrinter() {};
        }
    }

    private abstract class FunctionSyntaxTreePrinter implements Printer {

        protected String wrap(String content) {
            return content;
        }

        protected List<Node> getTargets(Tree tree) {
            return tree.getRootNode().getChildren();
        }

        protected Printer getAstPrinter() {
            return syntaxTreePrinter;
        }

        @Override
        public String print(Node node) {
            return print(List.of(node));
        }

        @Override
        public String print(Node... nodes) {
            return print(List.of(nodes));
        }

        @Override
        public String print(Collection<Node> nodes) {
            String content = nodePrinter.print(nodes);
            String wrapped = wrap(content);
            @Cleanup Tree tree = parser.parse(wrapped);
            List<Node> targets = getTargets(tree);
            Printer astPrinter = getAstPrinter();
            return astPrinter.print(targets);
        }
    }

    private static String readFile(Path path) throws IOException {
        @Cleanup Reader fileReader = new FileReader(path.toFile());
        @Cleanup Reader filterReader = new NullFilteredReader(fileReader);
        return CharStreams.toString(filterReader);
    }
}
