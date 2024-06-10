package ch.usi.si.seart.crawler.io;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.function.Predicate;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataSizePredicate implements Predicate<Path> {

    DataSize maxSize;

    @Override
    public boolean test(Path path) {
        try {
            long bytes = Files.size(path);
            DataSize size = DataSize.ofBytes(bytes);
            return size.compareTo(maxSize) < 0;
        } catch (NoSuchFileException ex) {
            return false;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
