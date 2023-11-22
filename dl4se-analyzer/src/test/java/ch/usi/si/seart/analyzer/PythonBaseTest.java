package ch.usi.si.seart.analyzer;

import ch.usi.si.seart.treesitter.Language;

import java.util.List;

public abstract class PythonBaseTest extends BaseTest {

    @Override
    protected Language getLanguage() {
        return Language.PYTHON;
    }

    @Override
    protected String getInput() {
        return
            "def main(arg):\n" +
            "    #line comment\n" +
            "    print(f\"Hello, {arg}!\")";
    }

    @Override
    protected List<String> getTokens() {
        return List.of(
                "def",
                "main",
                "(",
                "arg",
                ")",
                ":",
                "#line comment",
                "print",
                "(",
                "f\"Hello, {arg}!\"",
                ")"
        );
    }

    @Override
    protected List<String> getNodes() {
        return List.of(
                "def",
                "identifier",
                "(",
                "identifier",
                ")",
                ":",
                "comment",
                "identifier",
                "(",
                "string",
                ")"
        );
    }
}
