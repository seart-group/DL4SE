package usi.si.seart.analyzer.enumerator;

import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Range;

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
            case "builder": return Boilerplate.BUILDER;
            case "equals": return Boilerplate.EQUALS;
            case "hashCode": return Boilerplate.HASH_CODE;
            case "toString": return Boilerplate.TO_STRING;
            default: return null;
        }
    }
}
