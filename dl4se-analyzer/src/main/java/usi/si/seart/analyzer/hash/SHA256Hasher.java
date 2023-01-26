package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;
import usi.si.seart.treesitter.Node;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;

public abstract class SHA256Hasher implements Hasher {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_16LE;

    protected final MessageDigest md;

    @SneakyThrows(NoSuchAlgorithmException.class)
    protected SHA256Hasher() {
        this.md = MessageDigest.getInstance("SHA-256");
    }

    protected abstract void update(Node node);

    @Override
    public String hash(Node node) {
        return hash(new Node[] { node });
    }

    @Override
    public String hash(Collection<Node> nodes) {
        return hash(nodes.toArray(new Node[0]));
    }

    @Override
    public String hash(Node... nodes) {
        for (Node node: nodes) update(node);
        byte[] hash = md.digest();
        return bytesToHex(hash);
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexBuilder.append('0');
            hexBuilder.append(hex);
        }
        return hexBuilder.toString();
    }
}
