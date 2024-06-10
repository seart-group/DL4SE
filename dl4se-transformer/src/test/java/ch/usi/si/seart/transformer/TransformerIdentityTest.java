package ch.usi.si.seart.transformer;

public class TransformerIdentityTest extends BaseTest {

    @Override
    protected Transformer getTestSubject() {
        return Transformer.identity();
    }

    @Override
    protected String getTestInput() {
        return null;
    }

    @Override
    protected String getExpectedOutput() {
        return null;
    }
}
