package usi.si.seart.analyzer;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.Tree;
import usi.si.seart.analyzer.count.JavaTokenCounter;
import usi.si.seart.analyzer.enumerator.JavaBoilerplateEnumerator;
import usi.si.seart.analyzer.predicate.path.JavaTestFilePredicate;
import usi.si.seart.analyzer.printer.OffsetSyntaxTreePrinter;
import usi.si.seart.analyzer.printer.Printer;
import usi.si.seart.analyzer.query.multi.JavaMultiCaptureQueries;
import usi.si.seart.analyzer.query.single.JavaSingleCaptureQueries;

import java.nio.file.Path;
import java.util.List;

public class JavaAnalyzer extends AbstractAnalyzer {

    public JavaAnalyzer(LocalClone localClone, Path path) {
        super(localClone, path, Language.JAVA);
        this.totalTokenCounter = new JavaTokenCounter(this::getSourceBytes);
        this.testFilePredicate = new JavaTestFilePredicate();
        this.singleCaptureQueries = new JavaSingleCaptureQueries();
        this.multiCaptureQueries = new JavaMultiCaptureQueries();
        this.boilerplateEnumerator = new JavaBoilerplateEnumerator(this::getSourceBytes);
    }

    @Override
    protected String wrapContent(String content) {
        return "class _ {\n" + content + "\n}\n";
    }

    @Override
    protected List<Node> unwrapNodes(Tree tree) {
        Node root = tree.getRootNode();
        Node declaration = root.getChild(0);
        Node body = declaration.getChildByFieldName("body");
        return body.getChildren();
    }

    @Override
    protected Printer getAstPrinter() {
        return new OffsetSyntaxTreePrinter(new Point(-1, 0));
    }
}
