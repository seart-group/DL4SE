package usi.si.seart.analyzer;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import usi.si.seart.analyzer.count.CharacterCounter;
import usi.si.seart.analyzer.count.CodeTokenCounter;
import usi.si.seart.analyzer.count.Counter;
import usi.si.seart.analyzer.count.LineCounter;
import usi.si.seart.analyzer.count.TokenCounter;
import usi.si.seart.analyzer.enumerator.Enumerator;
import usi.si.seart.analyzer.hash.ContentHasher;
import usi.si.seart.analyzer.hash.Hasher;
import usi.si.seart.analyzer.hash.SyntaxTreeHasher;
import usi.si.seart.analyzer.predicate.node.ContainsErrorPredicate;
import usi.si.seart.analyzer.predicate.node.ContainsNonAsciiPredicate;
import usi.si.seart.analyzer.predicate.node.NodePredicate;
import usi.si.seart.analyzer.predicate.path.TestFilePredicate;
import usi.si.seart.analyzer.printer.NodePrinter;
import usi.si.seart.analyzer.printer.Printer;
import usi.si.seart.analyzer.printer.SymbolicExpressionPrinter;
import usi.si.seart.analyzer.printer.SyntaxTreePrinter;
import usi.si.seart.analyzer.query.multi.MultiCaptureQueries;
import usi.si.seart.analyzer.query.single.SingleCaptureQueries;
import usi.si.seart.analyzer.util.Tuple;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Query;
import usi.si.seart.treesitter.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractAnalyzer implements Analyzer {

    Language language;
    Parser parser;
    Tree tree;

    LocalClone localClone;
    Path path;
    String source;

    Counter lineCounter = new LineCounter();
    Counter codeTokenCounter = new CodeTokenCounter();
    Counter totalTokenCounter;
    Counter characterCounter;

    Hasher contentHasher;
    Hasher syntaxTreeHasher = new SyntaxTreeHasher();

    NodePredicate containsError = new ContainsErrorPredicate();
    NodePredicate containsNonAscii;

    TestFilePredicate testFilePredicate = new TestFilePredicate() {};

    Printer nodePrinter;
    Printer syntaxTreePrinter = new SyntaxTreePrinter();
    Printer expressionPrinter = new SymbolicExpressionPrinter();

    SingleCaptureQueries singleCaptureQueries = new SingleCaptureQueries(null) {
        @Override
        public void verify(Query query) {
        }

        @Override
        public List<Node> getComments(Node node) {
            return List.of();
        }

        @Override
        public List<Node> execute(Node node, String pattern) {
            return List.of();
        }
    };

    MultiCaptureQueries multiCaptureQueries = new MultiCaptureQueries(null) {
        @Override
        public void verify(Query query) {
        }

        @Override
        public List<List<Tuple<String, Node>>> getCallableDeclarations(Node node) {
            return List.of();
        }

        @Override
        public List<List<Tuple<String, Node>>> execute(Node node, String pattern) {
            return List.of();
        }
    };

    Enumerator<Boilerplate> boilerplateEnumerator = node -> null;

    @SneakyThrows(IOException.class)
    protected AbstractAnalyzer(LocalClone localClone, Path path, Language language) {
        this.language = language;
        this.parser = new Parser(language);
        this.localClone = localClone;
        this.path = path;
        this.source = Files.readString(path);
        this.tree = parser.parseString(source);
        NodeMapper mapper = this::getSourceBytes;
        this.totalTokenCounter = new TokenCounter(mapper){};
        this.characterCounter = new CharacterCounter(mapper);
        this.contentHasher = new ContentHasher(mapper);
        this.containsNonAscii = new ContainsNonAsciiPredicate(mapper);
        this.nodePrinter = new NodePrinter(mapper);
    }

    protected final byte[] getSourceBytes() {
        return source.getBytes(Settings.DEFAULT_CHARSET);
    }

    @Override
    public void close() {
        tree.close();
        parser.close();
    }

    @Override
    public final Result analyze() {
        File file = extractFileEntity();
        List<Function> functions = extractFunctionEntities(file);
        return new Analyzer.Result(file, functions);
    }

    protected File extractFileEntity() {
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

    protected List<Function> extractFunctionEntities(File file) {
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

    protected Function extractFunctionEntity(List<Tuple<String, Node>> match) {
        List<Node> nodes = match.stream()
                .map(Tuple::getValue)
                .collect(Collectors.toList());
        Node function = match.stream()
                .filter(tuple -> tuple.getKey().equals("target"))
                .map(Tuple::getValue)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        return Function.builder()
                .repo(localClone.getGitRepo())
                .ast(syntaxTreePrinter.print(nodes))
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
}
