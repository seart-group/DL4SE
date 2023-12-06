package ch.usi.si.seart.analyzer.enumerate;

import ch.usi.si.seart.model.code.Boilerplate;
import ch.usi.si.seart.treesitter.Node;

public class PythonBoilerplateEnumerator extends BoilerplateEnumerator {

    @Override
    public Boilerplate asEnum(Node node) {
        if (!node.getType().equals("function_definition")) return null;
        traverser: {
            for (Node prev = node.getPrevSibling(); prev != null; prev = prev.getPrevSibling()) {
                switch (prev.getType()) {
                    case "decorator":
                        String decorator = prev.getContent();
                        if (decorator.equals("@property")) return Boilerplate.GETTER;
                        else if (decorator.endsWith(".setter")) return Boilerplate.SETTER;
                    case "comment":
                        break;
                    default:
                        break traverser;
                }
            }
        }
        Node identifier = node.getChildByFieldName("name");
        String name = identifier.getContent();
        switch (name) {
            case "__new__":
                return Boilerplate.CONSTRUCTOR;
            case "__init__":
                return Boilerplate.INITIALIZER;
            case "__str__":
                return Boilerplate.STRING_CONVERSION;
            case "__repr__":
                return Boilerplate.STRING_REPRESENTATION;
            case "__hash__":
                return Boilerplate.HASHER;
            case "__get__":
            case "__getattr__":
                return Boilerplate.GETTER;
            case "__set__":
            case "__setattr__":
                return Boilerplate.SETTER;
            case "__del__":
            case "__delattr__":
                return Boilerplate.FINALIZER;
            case "__eq__":
            case "__ne__":
            case "__lt__":
            case "__le__":
            case "__gt__":
            case "__ge__":
                return Boilerplate.COMPARISON;
            case "__neg__":
            case "__pos__":
            case "__abs__":
            case "__invert__":
            case "__round__":
            case "__floor__":
            case "__ceil__":
            case "__trunc__":
                return Boilerplate.UNARY_ARITHMETIC;
            case "__add__":
            case "__sub__":
            case "__mul__":
            case "__div__":
            case "__truediv__":
            case "__floordiv__":
            case "__mod__":
            case "__pow__":
            case "__lshift__":
            case "__rshift__":
            case "__and__":
            case "__xor__":
            case "__or__":
                return Boilerplate.BINARY_ARITHMETIC;
            case "__iadd__":
            case "__isub__":
            case "__imul__":
            case "__imatmul__":
            case "__itruediv__":
            case "__ifloordiv__":
            case "__imod__":
            case "__ipow__":
            case "__ilshift__":
            case "__irshift__":
            case "__iand__":
            case "__ixor__":
            case "__ior__":
                return Boilerplate.AUGMENTED_ASSIGNMENT;
            default:
                return null;
        }
    }
}
