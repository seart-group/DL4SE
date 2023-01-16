package usi.si.seart.analyzer.printer;

import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Range;

import java.util.List;
import java.util.stream.Collectors;

public class NodePrinter extends ContentPrinter {

    public NodePrinter(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    public String print(Node node) {
        Range range = node.getRange();
        String content = mapper.getContentForRange(range);
        int offset = node.getStartPoint().getColumn();
        List<String> lines = content.lines().collect(Collectors.toList());
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() > offset)
                lines.set(i, line.substring(offset));
        }
        return String.join("\n", lines);
    }

}
