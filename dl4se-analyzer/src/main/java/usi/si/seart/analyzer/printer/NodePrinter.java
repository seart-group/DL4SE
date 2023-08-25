package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import usi.si.seart.analyzer.NodeMapper;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class NodePrinter extends ContentPrinter {

    public NodePrinter(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    public String print(Node node) {
        Range range = node.getRange();
        String content = mapper.getContentForRange(range);
        List<String> lines = content.lines()
                .map(line -> line.replace("\t", "    "))
                .collect(Collectors.toList());
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int spaces = countStartSpaces(line);
            int column = node.getStartPoint().getColumn();
            int offset = Math.min(spaces, column);
            lines.set(i, line.substring(offset));
        }
        return String.join("\n", lines);
    }

    @Override
    protected Collector<CharSequence, ?, String> resultCollector() {
        return Collectors.joining("\n");
    }

    private int countStartSpaces(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') count++;
            else break;
        }
        return count;
    }
}
