package usi.si.seart.analyzer.enumerator;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.model.code.Boilerplate;

public class JavaBoilerplateEnumerator extends BoilerplateEnumerator {

    public JavaBoilerplateEnumerator(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    public Boilerplate asEnum(Node node) {
        String type = node.getType();
        if (type.equals("constructor_declaration")) return Boilerplate.CONSTRUCTOR;
        if (!type.equals("method_declaration")) return null;
        Node identifier = node.getChildByFieldName("name");
        Range range = identifier.getRange();
        String name = mapper.getContentForRange(range);
        if (name.startsWith("set")) return Boilerplate.SETTER;
        if (name.startsWith("get")) return Boilerplate.GETTER;
        switch (name) {
            case "equals":
            case "compareTo":
                return Boilerplate.COMPARISON;
            case "clone":
                return Boilerplate.CLONER;
            case "finalize":
                return Boilerplate.FINALIZER;
            case "hashCode":
                return Boilerplate.HASHER;
            case "readObject":
            case "readObjectNoData":
                return Boilerplate.DESERIALIZER;
            case "toString":
                return Boilerplate.STRING_CONVERSION;
            case "writeObject":
                return Boilerplate.SERIALIZER;
            default:
                return null;
        }
    }
}
