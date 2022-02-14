package usi.si.seart.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class PathUtils {

    /**
     * File utility used to forcefully delete a directory and all its contents.
     *
     * @implNote We suppress throws of {@link java.io.IOException IOException} and {@link InterruptedException}.
     * @author dabico
     * @see ProcessBuilder
     * @see <a href="https://www.baeldung.com/run-shell-command-in-java#ProcessBuilder">Run Shell Commands in Java</a>
     */
    @SneakyThrows
    public void forceDelete(Path file) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("rm", "-rf", file.getFileName().toString());
        builder.directory(file.getParent().toFile());
        Process process = builder.start();
        process.waitFor();
    }
}
