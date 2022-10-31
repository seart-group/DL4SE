package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.Language;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.FileLanguageCountRepository;
import usi.si.seart.repository.FunctionLanguageCountRepository;
import usi.si.seart.repository.GitRepoLanguageCountRepository;
import usi.si.seart.repository.LanguageRepository;
import usi.si.seart.repository.TableCountsRepository;
import usi.si.seart.repository.TaskRepository;
import usi.si.seart.views.TableCount;
import usi.si.seart.views.language.LanguageCount;

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

        TableCountsRepository tableCountsRepository;

        GitRepoLanguageCountRepository gitRepoLanguageCountRepository;
        FileLanguageCountRepository fileLanguageCountRepository;
        FunctionLanguageCountRepository functionLanguageCountRepository;

        LanguageRepository languageRepository;
        TaskRepository taskRepository;

        @Override
        public Long countUsers() {
            return tableCountsRepository.findById("user")
                    .map(TableCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Long countGitRepos() {
            return tableCountsRepository.findById("git_repo")
                    .map(TableCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Long countFiles() {
            return tableCountsRepository.findById("file")
                    .map(TableCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Long countFunctions() {
            return tableCountsRepository.findById("function")
                    .map(TableCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Map<Language, Long> countGitReposByLanguage() {
            return getLanguageCount(gitRepoLanguageCountRepository::findAll);
        }

        @Override
        public Map<Language, Long> countFilesByLanguage() {
            return getLanguageCount(fileLanguageCountRepository::findAll);
        }

        @Override
        public Map<Language, Long> countFunctionsByLanguage() {
            return getLanguageCount(functionLanguageCountRepository::findAll);
        }

        private Map<Language, Long> getLanguageCount(Supplier<List<? extends LanguageCount>> supplier) {
            Stream<Map.Entry<Language, Long>> languagesEmpty = languageRepository.findAll().stream()
                    .map(language -> Map.entry(language, 0L));
            Stream<Map.Entry<Language, Long>> languagesData = supplier.get().stream()
                    .map(entity -> Map.entry(entity.getLanguage(), entity.getCount()));
            return Stream.concat(languagesEmpty, languagesData).collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2)
            );
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
