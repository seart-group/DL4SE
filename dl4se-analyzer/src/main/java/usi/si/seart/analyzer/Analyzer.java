package usi.si.seart.analyzer;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Parser;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.Tree;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import usi.si.seart.analyzer.count.CharacterCounter;
import usi.si.seart.analyzer.count.CodeTokenCounter;
import usi.si.seart.analyzer.count.Counter;
import usi.si.seart.analyzer.count.LineCounter;
import usi.si.seart.analyzer.count.TokenCounter;
import usi.si.seart.analyzer.enumerator.BoilerplateEnumerator;
import usi.si.seart.analyzer.enumerator.Enumerator;
import usi.si.seart.analyzer.hash.ContentHasher;
import usi.si.seart.analyzer.hash.Hasher;
import usi.si.seart.analyzer.hash.SyntaxTreeHasher;
import usi.si.seart.analyzer.predicate.node.ContainsErrorPredicate;
import usi.si.seart.analyzer.predicate.node.ContainsNonAsciiPredicate;
import usi.si.seart.analyzer.predicate.node.NodePredicate;
import usi.si.seart.analyzer.predicate.path.TestFilePredicate;
import usi.si.seart.analyzer.printer.NodePrinter;
import usi.si.seart.analyzer.printer.OffsetSyntaxTreePrinter;
import usi.si.seart.analyzer.printer.Printer;
import usi.si.seart.analyzer.printer.SymbolicExpressionPrinter;
import usi.si.seart.analyzer.printer.SyntaxTreePrinter;
import usi.si.seart.analyzer.query.multi.MultiCaptureQueries;
import usi.si.seart.analyzer.util.Tuple;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class Analyzer implements AutoCloseable {

    Language language;
    Parser parser;
    Tree tree;

    LocalClone localClone;
    Path path;
    String source;

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

    public Analyzer(LocalClone localClone, Path path, String languageName) throws IOException {
        this(localClone, path, Language.valueOf(languageName));
    }

    public Analyzer(LocalClone localClone, Path path, Language language) throws IOException {
        this.language = language;
        this.parser = new Parser(language);
        this.localClone = localClone;
        this.path = path;
        this.source = Files.readString(path);
        this.tree = parser.parseString(source);
        NodeMapper mapper = () -> source.getBytes(NodeMapper.DEFAULT_CHARSET);
        this.lineCounter = new LineCounter();
        this.totalTokenCounter = TokenCounter.getInstance(language, mapper);
        this.codeTokenCounter = CodeTokenCounter.getInstance(language);
        this.characterCounter = new CharacterCounter(mapper);
        this.contentHasher = new ContentHasher(mapper);
        this.syntaxTreeHasher = new SyntaxTreeHasher();
        this.containsError = new ContainsErrorPredicate();
        this.containsNonAscii = new ContainsNonAsciiPredicate(mapper);
        this.testFilePredicate = TestFilePredicate.getInstance(language);
        this.nodePrinter = new NodePrinter(mapper);
        this.syntaxTreePrinter = new SyntaxTreePrinter();
        this.expressionPrinter = new SymbolicExpressionPrinter();
        this.multiCaptureQueries = MultiCaptureQueries.getInstance(language);
        this.boilerplateEnumerator = BoilerplateEnumerator.getInstance(language, mapper);
    }

    @Override
    public void close() {
        tree.close();
        parser.close();
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
        List<List<Tuple<String, Node>>> matches = multiCaptureQueries.getCallableDeclarations(tree.getRootNode());
        List<Function> functions = new ArrayList<>(matches.size());
        for (List<Tuple<String, Node>> match: matches) {
            Function function = extractFunctionEntity(match);
            // These operations are invariant by
            // nature and should not be overridden
            function.setFile(file);
            function.setIsTest(file.getIsTest());
            functions.add(function);
        }
        return functions;
    }

    private Function extractFunctionEntity(List<Tuple<String, Node>> match) {
        List<Node> nodes = match.stream()
                .map(Tuple::getValue)
                .collect(Collectors.toList());
        Node function = match.stream()
                .filter(tuple -> tuple.getKey().equals("target"))
                .map(Tuple::getValue)
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
        @SneakyThrows(UnsupportedEncodingException.class)
        public String print(Collection<Node> nodes) {
            String content = nodePrinter.print(nodes);
            String wrapped = wrap(content);
            @Cleanup Tree tree = parser.parseString(wrapped);
            List<Node> targets = getTargets(tree);
            Printer astPrinter = getAstPrinter();
            return astPrinter.print(targets);
        }
    }
}
