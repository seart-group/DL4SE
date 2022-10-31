package usi.si.seart.scheduling;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.GitRepo;
import usi.si.seart.service.GitRepoService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RepoMaintainer implements Runnable {

    GitRepoService gitRepoService;

    @Override
    public void run() {
        log.debug("Running repository maintenance...");
        gitRepoService.forEach(this::checkExists);
        log.debug("Repository maintenance complete!");
    }

    @SneakyThrows({IOException.class, InterruptedException.class})
    private void checkExists(GitRepo gitRepo) {
        String name = gitRepo.getName();
        String url = String.format("https://github.com/%s", name);

        log.trace("Checking if Git repository exists: [{}]", name);

        ProcessBuilder pb = new ProcessBuilder("git", "ls-remote", url);
        pb.environment().put("GIT_TERMINAL_PROMPT", "0");
        Process process = pb.start();
        long pid = process.pid();

        boolean isUnavailable = gitRepo.getIsUnavailable();
        boolean exited = process.waitFor(60, TimeUnit.SECONDS);
        if (exited) {
            isUnavailable = process.exitValue() != 0;
        } else {
            log.trace("Attempting to terminate timed-out process: [{}]", pid);
            while (process.isAlive()) process.destroyForcibly();
            log.trace("Terminated timed-out process: [{}]", pid);
        }

        gitRepo.setIsUnavailable(isUnavailable);
    }
}
