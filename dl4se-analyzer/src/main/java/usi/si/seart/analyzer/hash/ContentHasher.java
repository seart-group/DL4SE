package usi.si.seart.analyzer.hash;

import lombok.Cleanup;
import lombok.Getter;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.TreeCursor;

import java.util.Arrays;

@Getter
public class ContentHasher extends SHA256Hasher implements ByteArrayBacked {

    private byte[] bytes;
    private boolean ready;

    public ContentHasher() {
        super();
        this.bytes = new byte[0];
        this.ready = false;
    }

    public ContentHasher(byte[] bytes) {
        super();
        this.bytes = bytes;
        this.ready = true;
    }

    @Override
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        this.ready = bytes.length > 0;
    }

    @Override
    protected String sha256(Node node) {
        if (!ready) return null;
        @Cleanup TreeCursor cursor = node.walk();
        cursor.preorderTraversal(current -> {
            boolean leafNode = current.getChildCount() == 0;
            boolean isComment = current.getType().contains("comment");
            if (leafNode && !isComment) {
                int startByte = current.getStartByte() * 2;
                int endByte = current.getEndByte() * 2;
                md.update(Arrays.copyOfRange(this.bytes, startByte, endByte));
            }
        });

        byte[] hash = md.digest();
        return bytesToHex(hash);
    }
}
