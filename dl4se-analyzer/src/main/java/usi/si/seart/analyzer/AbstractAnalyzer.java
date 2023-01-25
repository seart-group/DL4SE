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
import usi.si.seart.analyzer.predicate.ContainsErrorPredicate;
import usi.si.seart.analyzer.predicate.ContainsNonAsciiPredicate;
import usi.si.seart.analyzer.predicate.TestFilePredicate;
import usi.si.seart.analyzer.printer.NodePrinter;
import usi.si.seart.analyzer.printer.Printer;
import usi.si.seart.analyzer.printer.SExpressionPrinter;
import usi.si.seart.analyzer.printer.SyntaxTreePrinter;
import usi.si.seart.analyzer.query.multi.MultiCaptureQueries;
import usi.si.seart.analyzer.query.single.SingleCaptureQueries;
import usi.si.seart.analyzer.util.Tuple;
import usi.si.seart.analyzer.util.stream.DelimiterSuffixedStringCollector;
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
import java.util.NoSuchElementException;
import java.util.function.Predicate;

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

    Predicate<Node> containsError = new ContainsErrorPredicate();
    Predicate<Node> containsNonAscii;

    Predicate<Path> testFilePredicate = new TestFilePredicate() {};

    Printer nodePrinter;
    Printer syntaxTreePrinter = new SyntaxTreePrinter();
    Printer sExpressionPrinter = new SExpressionPrinter();

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

    @SneakyThrows({IOException.class})
    protected AbstractAnalyzer(LocalClone localClone, Path path, Language language) {
        this.language = language;
        this.parser = new Parser(language);
        this.localClone = localClone;
        this.path = path;
        this.source = Files.readString(path);
        this.tree = parser.parseString(source);
        NodeMapper mapper = this::getSourceBytes;
        this.totalTokenCounter = new TokenCounter(mapper);
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
                .sExpression("(sexp " + sExpressionPrinter.print(node) + ")")
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
        List<List<Tuple<String, Node>>> targets = multiCaptureQueries.getCallableDeclarations(tree.getRootNode());
        List<Function> functions = new ArrayList<>(targets.size());
        for (List<Tuple<String, Node>> nodes: targets) {
            Function function = extractFunctionEntity(nodes);
            // These operations are invariant by
            // nature and should not be overridden
            function.setFile(file);
            function.setIsTest(file.getIsTest());
            functions.add(function);
        }
        return functions;
    }

    protected Function extractFunctionEntity(List<Tuple<String, Node>> nodes) {
        Node target = nodes.stream()
                .filter(tuple -> tuple.getKey().equals("target"))
                .map(Tuple::getValue)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        String content = nodes.stream()
                .map(Tuple::getValue)
                .map(node -> nodePrinter.print(node))
                .collect(new DelimiterSuffixedStringCollector("\n"));
        String ast = nodes.stream()
                .map(Tuple::getValue)
                .map(node -> syntaxTreePrinter.print(node))
                .collect(new DelimiterSuffixedStringCollector("\n"));
        String sExp = nodes.stream()
                .map(Tuple::getValue)
                .map(node -> sExpressionPrinter.print(node))
                .collect(new DelimiterSuffixedStringCollector(" "));

        Long sumCodeTokens = nodes.stream()
                .map(Tuple::getValue)
                .mapToLong(node -> codeTokenCounter.count(node))
                .sum();
        Long sumTotalTokens = nodes.stream()
                .map(Tuple::getValue)
                .mapToLong(node -> totalTokenCounter.count(node))
                .sum();
        Long sumLines = nodes.stream()
                .map(Tuple::getValue)
                .mapToLong(node -> lineCounter.count(node))
                .sum();
        Long sumCharacters = nodes.stream()
                .map(Tuple::getValue)
                .mapToLong(node -> characterCounter.count(node))
                .sum();
        boolean anyContainsNonAscii = nodes.stream()
                .map(Tuple::getValue)
                .anyMatch(node -> containsNonAscii.test(node));
        boolean anyContainsError = nodes.stream()
                .map(Tuple::getValue)
                .anyMatch(node -> containsError.test(node));

        return Function.builder()
                .repo(localClone.getGitRepo())
                .ast(ast)
                .content(content)
                .astHash(syntaxTreeHasher.hash(target))
                .contentHash(contentHasher.hash(target))
                .sExpression("(sexp " + sExp + ")")
                .totalTokens(sumTotalTokens)
                .codeTokens(sumCodeTokens)
                .lines(sumLines)
                .characters(sumCharacters)
                .containsNonAscii(anyContainsNonAscii)
                .containsError(anyContainsError)
                .boilerplateType(boilerplateEnumerator.asEnum(target))
                .build();
    }
}
