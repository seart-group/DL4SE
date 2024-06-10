package ch.usi.si.seart.analyzer;

import ch.usi.si.seart.analyzer.count.CharacterCounter;
import ch.usi.si.seart.analyzer.count.CodeTokenCounter;
import ch.usi.si.seart.analyzer.count.Counter;
import ch.usi.si.seart.analyzer.count.LineCounter;
import ch.usi.si.seart.analyzer.count.TokenCounter;
import ch.usi.si.seart.analyzer.enumerate.BoilerplateEnumerator;
import ch.usi.si.seart.analyzer.enumerate.Enumerator;
import ch.usi.si.seart.analyzer.extract.Extractor;
import ch.usi.si.seart.analyzer.extract.FunctionExtractorFactory;
import ch.usi.si.seart.analyzer.hash.ContentHasher;
import ch.usi.si.seart.analyzer.hash.Hasher;
import ch.usi.si.seart.analyzer.hash.SyntaxTreeHasher;
import ch.usi.si.seart.analyzer.predicate.node.ContainsErrorPredicate;
import ch.usi.si.seart.analyzer.predicate.node.ContainsNonAsciiPredicate;
import ch.usi.si.seart.analyzer.predicate.node.NodePredicate;
import ch.usi.si.seart.analyzer.predicate.path.TestFilePredicate;
import ch.usi.si.seart.analyzer.printer.NodePrinter;
import ch.usi.si.seart.analyzer.printer.OffsetSyntaxTreePrinter;
import ch.usi.si.seart.analyzer.printer.Printer;
import ch.usi.si.seart.analyzer.printer.SymbolicExpressionPrinter;
import ch.usi.si.seart.analyzer.printer.SyntaxTreePrinter;
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

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;
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

    Extractor functionExtractor;

    Enumerator<Boilerplate> boilerplateEnumerator;

    ExecutorService executorService = Executors.newFixedThreadPool(3);

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
        this.functionExtractor = FunctionExtractorFactory.getInstance(language);
        this.boilerplateEnumerator = BoilerplateEnumerator.getInstance(language);
    }

    @Override
    public void close() {
        tree.close();
        parser.close();
        executorService.shutdown();
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
        File.FileBuilder<?, ?> builder = File.builder()
                .repo(localClone.getGitRepo())
                .path(localClone.relativePathOf(path).toString());
        CompletableFuture.allOf(
                supplyAsync(() -> nodePrinter.print(node)).thenAcceptAsync(builder::content),
                supplyAsync(() -> syntaxTreePrinter.print(node)).thenAcceptAsync(builder::ast),
                supplyAsync(() -> expressionPrinter.print(node)).thenAcceptAsync(builder::symbolicExpression),
                supplyAsync(() -> contentHasher.hash(node)).thenAcceptAsync(builder::contentHash),
                supplyAsync(() -> syntaxTreeHasher.hash(node)).thenAcceptAsync(builder::astHash),
                supplyAsync(() -> totalTokenCounter.count(node)).thenAcceptAsync(builder::totalTokens),
                supplyAsync(() -> codeTokenCounter.count(node)).thenAcceptAsync(builder::codeTokens),
                supplyAsync(() -> lineCounter.count(node)).thenAcceptAsync(builder::lines),
                supplyAsync(() -> characterCounter.count(node)).thenAcceptAsync(builder::characters),
                supplyAsync(() -> containsNonAscii.test(node)).thenAcceptAsync(builder::containsNonAscii),
                supplyAsync(() -> containsError.test(node)).thenAcceptAsync(builder::containsError),
                supplyAsync(() -> testFilePredicate.test(path)).thenAcceptAsync(builder::isTest)
        ).join();
        return builder.build();
    }

    private List<Function> extractFunctionEntities(File file) {
        List<Extractor.Match> matches = functionExtractor.extract(tree);
        List<Function> functions = matches.stream()
                .map(this::extractFunctionEntity)
                .collect(Collectors.toUnmodifiableList());
        functions.forEach(function -> function.setFile(file));
        return functions;
    }

    private Function extractFunctionEntity(Extractor.Match match) {
        List<Node> nodes = match.getNodes();
        Node function = match.getTarget();
        Printer standalone = getStandalonePrinter();
        Function.FunctionBuilder<?, ?> builder = Function.builder()
                .repo(localClone.getGitRepo());
        CompletableFuture.allOf(
                supplyAsync(() -> nodePrinter.print(nodes)).thenAcceptAsync(builder::content),
                supplyAsync(() -> standalone.print(nodes)).thenAcceptAsync(builder::ast),
                supplyAsync(() -> expressionPrinter.print(nodes)).thenAcceptAsync(builder::symbolicExpression),
                supplyAsync(() -> contentHasher.hash(nodes)).thenAcceptAsync(builder::contentHash),
                supplyAsync(() -> syntaxTreeHasher.hash(nodes)).thenAcceptAsync(builder::astHash),
                supplyAsync(() -> totalTokenCounter.count(nodes)).thenAcceptAsync(builder::totalTokens),
                supplyAsync(() -> codeTokenCounter.count(nodes)).thenAcceptAsync(builder::codeTokens),
                supplyAsync(() -> lineCounter.count(nodes)).thenAcceptAsync(builder::lines),
                supplyAsync(() -> characterCounter.count(nodes)).thenAcceptAsync(builder::characters),
                supplyAsync(() -> containsNonAscii.test(nodes)).thenAcceptAsync(builder::containsNonAscii),
                supplyAsync(() -> containsError.test(nodes)).thenAcceptAsync(builder::containsError),
                supplyAsync(() -> boilerplateEnumerator.asEnum(function)).thenAcceptAsync(builder::boilerplateType)
        ).join();
        return builder.build();
    }

    private <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
        return CompletableFuture.supplyAsync(supplier, executorService);
    }

    private FunctionSyntaxTreePrinter getStandalonePrinter() {
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
