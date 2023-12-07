package ch.usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Node;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.Collection;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class SHA256Hasher implements Hasher {

    MessageDigest md = DigestUtils.getSha256Digest();

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
        return Hex.encodeHexString(hash);
    }
}
