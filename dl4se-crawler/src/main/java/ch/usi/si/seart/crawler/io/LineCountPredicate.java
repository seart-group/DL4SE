package ch.usi.si.seart.crawler.io;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Predicate;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LineCountPredicate implements Predicate<Path> {

    long maxLines;

    @Override
    public boolean test(Path path) {
        try (
            FileReader fileReader = new FileReader(path.toFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            int lines = 0;
            while (bufferedReader.readLine() != null) {
                lines++;
                if (lines >= maxLines) return false;
            }
            return true;
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
