package usi.si.seart.treesitter;

import lombok.experimental.StandardException;

/**
 * An exception thrown whenever there is an ABI version mismatch.
 * <p/>
 * These are usually raised as a result of a language being
 * generated with an incompatible version of the Tree-sitter CLI.
 */
@StandardException
public class ABIVersionMismatch extends TreeSitterException {
}
