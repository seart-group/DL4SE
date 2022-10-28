package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.Language;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.FileRepository;
import usi.si.seart.repository.FunctionRepository;
import usi.si.seart.repository.GitRepoRepository;
import usi.si.seart.repository.TaskRepository;
import usi.si.seart.repository.UserRepository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StatisticsService {

    Long countUsers();
    Long countGitRepos();
    Long countFiles();
    Long countFunctions();
    Map<Language, Long> countGitReposByLanguage();
    Map<Language, Long> countFilesByLanguage();
    Map<Language, Long> countFunctionsByLanguage();
    Long countTasks();
    Long countTasks(User user);
    Map<Status, Long> countTasksByStatus();
    Map<Status, Long> countTasksByStatus(User user);
    Long getTotalTaskSize();
    Long getTotalTaskSize(User user);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class StatisticsServiceImpl implements StatisticsService {

        UserRepository userRepository;
        GitRepoRepository gitRepoRepository;
        FileRepository fileRepository;
        FunctionRepository functionRepository;
        TaskRepository taskRepository;

        @Override
        public Long countUsers() {
            return userRepository.count();
        }

        @Override
        public Long countGitRepos() {
            return gitRepoRepository.count();
        }

        @Override
        public Long countFiles() {
            return fileRepository.count();
        }

        @Override
        public Long countFunctions() {
            return functionRepository.count();
        }

        @Override
        public Map<Language, Long> countGitReposByLanguage() {
            return gitRepoRepository.countAllGroupByLanguage().stream()
                    .map(tuple -> Map.entry(tuple.get(0, Language.class), tuple.get(1, Long.class)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        @Override
        public Map<Language, Long> countFilesByLanguage() {
            return fileRepository.countAllGroupByLanguage().stream()
                    .map(tuple -> Map.entry(tuple.get(0, Language.class), tuple.get(1, Long.class)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        @Override
        public Map<Language, Long> countFunctionsByLanguage() {
            return functionRepository.countAllGroupByLanguage().stream()
                    .map(tuple -> Map.entry(tuple.get(0, Language.class), tuple.get(1, Long.class)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        @Override
        public Long countTasks() {
            return taskRepository.count();
        }

        @Override
        public Long countTasks(User user) {
            return taskRepository.countAllByUser(user);
        }

        @Override
        public Map<Status, Long> countTasksByStatus() {
            return supplyToMap(taskRepository::countAllGroupByStatus);
        }

        @Override
        public Map<Status, Long> countTasksByStatus(User user) {
            return supplyToMap(() -> taskRepository.countAllByUserGroupByStatus(user));
        }

        @Override
        public Long getTotalTaskSize() {
            return taskRepository.sumSize();
        }

        @Override
        public Long getTotalTaskSize(User user) {
            return taskRepository.sumSizeByUser(user);
        }

        private Map<Status, Long> supplyToMap(Supplier<List<Tuple>> tupleResultQuery) {
            Stream<Map.Entry<Status, Long>> noResultStream = Stream.of(Status.values())
                    .map(status -> Map.entry(status, 0L));
            Stream<Map.Entry<Status, Long>> queryResultStream = tupleResultQuery.get().stream()
                    .map(tuple -> Map.entry(tuple.get(0, Status.class), tuple.get(1, Long.class)));
            return Stream.concat(noResultStream, queryResultStream).collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, TreeMap::new)
            );
        }
    }
}
