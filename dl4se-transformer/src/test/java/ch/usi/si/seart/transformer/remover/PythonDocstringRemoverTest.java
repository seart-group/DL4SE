package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.transformer.PythonBaseTest;
import ch.usi.si.seart.transformer.Transformer;

class PythonDocstringRemoverTest extends PythonBaseTest {

    @Override
    protected Transformer getTestSubject() {
        return new PythonDocstringRemover();
    }

    @Override
    protected String getTestInput() {
        return "\"\"\"module docstring\"\"\"\n" +
                "\n" +
                "# comment\n" +
                "def func(x): # comment\n" +
                "    \"\"\"Function docstring\"\"\"\n" +
                "    pass # comment\n" +
                "    \n" +
                "class Example:\n" +
                "    # comment\n" +
                "    # comment\n" +
                "    \"\"\"Class docstring\"\"\"\n" +
                "    pass\n";
    }

    @Override
    protected String getExpectedOutput() {
        return "\n" +
                "\n" +
                "# comment\n" +
                "def func(x): # comment\n" +
                "    \n" +
                "    pass # comment\n" +
                "    \n" +
                "class Example:\n" +
                "    # comment\n" +
                "    # comment\n" +
                "    \n" +
                "    pass\n";
    }
}
