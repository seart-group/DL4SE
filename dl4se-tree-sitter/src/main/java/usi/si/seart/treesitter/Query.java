package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * A query consists of one or more patterns, where each pattern is an S-expression that matches a certain set of nodes
 * in a syntax tree. The query is associated with a particular language, and can only be run on syntax nodes parsed with
 * that language. The expression to match a given node consists of a pair of parentheses containing two things: the
 * node's type, and optionally, a series of other S-expressions that match the node's children. For example, this
 * pattern would match any {@code binary_expression} node whose children are both {@code number_literal} nodes:
 *
 * <pre>
 *     (binary_expression (number_literal) (number_literal))
 * </pre>
 *
 * Children can also be omitted. For example, this would match any {@code binary_expression} where at least one of
 * child is a {@code string_literal} node:
 *
 * <pre>
 *     (binary_expression (string_literal))
 * </pre>
 *
 * @apiNote The underlying query value is immutable and can be safely shared between threads.
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Query extends External {

    Language language;
    String sExpr;

    public Query(Language language, String sExpr) {
        super(createIfValid(language, sExpr));
        this.language = language;
        this.sExpr = sExpr;
    }

    private static long createIfValid(Language language, String sExpr) {
        Languages.validate(language);
        Objects.requireNonNull(sExpr, "Pattern must not be null!");
        long pointer = TreeSitter.queryNew(language.getId(), sExpr);
        if (pointer != 0L) return pointer;
        else throw new SymbolicExpressionException("Invalid S-Expression pattern: " + sExpr);
    }

    /**
     * Delete a query, freeing all the memory that it used.
     */
    @Override
    public void close() {
        TreeSitter.queryDelete(pointer);
    }

    @Override
    public String toString() {
        return "Query(language: "+language.name()+", expression: '"+sExpr+"')";
    }
}
