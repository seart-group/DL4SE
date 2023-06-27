package usi.si.seart.analyzer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.GitRepo;

import java.nio.file.Path;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public final class LocalClone {

    GitRepo gitRepo;
    Path diskPath;

    public Path relativePathOf(Path path) {
        return diskPath.relativize(path);
    }
}
