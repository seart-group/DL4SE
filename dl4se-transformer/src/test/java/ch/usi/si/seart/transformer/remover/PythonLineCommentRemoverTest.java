package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.transformer.PythonBaseTest;
import ch.usi.si.seart.transformer.Transformer;

class PythonLineCommentRemoverTest extends PythonBaseTest {

    @Override
    protected Transformer getTestSubject() {
        return new PythonLineCommentRemover();
    }

    @Override
    protected String getTestInput() {
        return "# comment\n" +
                "def func(x):\n" +
                "    \"\"\"Function docstring\"\"\"\n" +
                "    pass # comment\n" +
                "# \n";
    }

    @Override
    protected String getExpectedOutput() {
        return "\n" +
                "def func(x):\n" +
                "    \"\"\"Function docstring\"\"\"\n" +
                "    pass \n" +
                "\n";
    }
}
