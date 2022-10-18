package usi.si.seart.patch;


import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.model.code.Code;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class HashPatch {

    private static final SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public static void main(String[] args) {
        long start = 0L;
        if (args.length > 0)
            start = Long.parseLong(args[0]);

        String sqlQuery = "SELECT c FROM Code c WHERE c.id >= :start ORDER BY c.id ASC";
        @Cleanup StatelessSession session = factory.openStatelessSession();
        @Cleanup ScrollableResults results = session.createQuery(sqlQuery, Code.class)
                .setParameter("start", start)
                .setCacheable(false)
                .setFetchSize(500)
                .scroll(ScrollMode.FORWARD_ONLY);

        while (results.next()) {
            Code code = (Code) results.get(0);

            java.util.function.Function<String, Node> parser;
            if (code instanceof File) {
                parser = StaticJavaParser::parse;
                File file = (File) code;
                if (!file.getIsParsed()) {
                    log.debug("Skipping {} [ID: {}]", code.getClass().getSimpleName(), code.getId());
                    continue;
                }
            } else {
                parser = StaticJavaParser::parseMethodDeclaration;
                Function function = (Function) code;
                Boilerplate boilerplate = function.getBoilerplateType();
                if (Boilerplate.CONSTRUCTOR.equals(boilerplate)) {
                    parser = StaticJavaParser::parseBodyDeclaration;
                }
            }

            String content = code.getContent();

            Node node;
            String normalized;
            try {
                node = parser.apply(content);
                List<Comment> comments = node.getAllContainedComments();
                node.getComment().ifPresent(comments::add);
                comments.forEach(Comment::remove);
                normalized = normalizeSpace(node.toString());
            } catch (ParseProblemException | StackOverflowError ignored) {
                log.error("Hash update failed for {} [ID: {}]", code.getClass().getSimpleName(), code.getId());
                continue;
            }

            String oldHash = code.getContentHash();
            String newHash = sha256(normalized);

            if (!oldHash.equals(newHash)) {
                log.info("Updating {} hash [ID: {}]", code.getClass().getSimpleName(), code.getId());
                log.debug("\tWas: {}", oldHash);
                log.debug("\tNow: {}", newHash);

                Session secondary = factory.getCurrentSession();
                Transaction transaction = secondary.beginTransaction();
                code.setContentHash(newHash);
                secondary.update(code);
                secondary.flush();
                transaction.commit();
            } else {
                log.debug("Skipping {} [ID: {}]", code.getClass().getSimpleName(), code.getId());
            }
        }
    }

    private static String normalizeSpace(String input) {
        Objects.requireNonNull(input);
        if (input.isBlank()) return "";

        StringBuilder builder = new StringBuilder(input.length());
        boolean lastWasWhitespace = false;
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (Character.isWhitespace(current)) {
                if (!lastWasWhitespace) {
                    lastWasWhitespace = true;
                    builder.append(' ');
                }
            } else {
                lastWasWhitespace = false;
                builder.append(current);
            }
        }

        return builder.toString().trim();
    }

    @SneakyThrows({NoSuchAlgorithmException.class})
    private static String sha256(String input) {
        Objects.requireNonNull(input);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
