package usi.si.seart.analyzer.test;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.LibraryLoader;
import ch.usi.si.seart.treesitter.Parser;
import ch.usi.si.seart.treesitter.Tree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import usi.si.seart.analyzer.NodeMapper;

import java.nio.charset.Charset;
import java.util.List;

public abstract class BaseTest {

    static {
        LibraryLoader.load();
    }

    protected Parser parser;
    protected Tree tree;

    @BeforeEach
    protected void setUp() {
        setUpParser();
        setUpTree();
    }

    void setUpParser() {
        parser = new Parser(getLanguage());
    }

    void setUpTree() {
        tree = parser.parse(getInput());
    }

    @AfterEach
    void tearDown() {
        parser.close();
        tree.close();
    }

    protected abstract Language getLanguage();

    protected abstract String getInput();

    protected byte[] getBytes() {
        return getInput().getBytes(getCharset());
    }

    protected Charset getCharset() {
        return NodeMapper.DEFAULT_CHARSET;
    }

    protected NodeMapper getNodeMapper() {
        return this::getBytes;
    }

    protected abstract List<String> getTokens();

    protected String getJoinedTokens() {
        return String.join("", getTokens());
    }

    protected abstract List <String> getNodes();

    protected String getJoinedNodes() {
        return String.join("", getNodes());
    }
}
