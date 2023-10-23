package ch.usi.si.seart.transformer;

import ch.usi.si.seart.transformer.remover.JavaDocumentationCommentRemover;
import ch.usi.si.seart.transformer.remover.JavaLineCommentRemover;
import ch.usi.si.seart.transformer.wrapper.JavaDummyClassWrapper;

public class TransformerNestedChainTest extends TransformerChainTest {

    @Override
    protected Transformer getTestSubject() {
        return Transformer.chain(
                new JavaDummyClassWrapper(),
                Transformer.chain(
                        new JavaLineCommentRemover(),
                        new JavaDocumentationCommentRemover()
                )
        );
    }
}
