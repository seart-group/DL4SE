package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.Language;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.CodeRepository;
import usi.si.seart.repository.FileCountByLanguageRepository;
import usi.si.seart.repository.FunctionCountByLanguageRepository;
import usi.si.seart.repository.GitRepoCountByLanguageRepository;
import usi.si.seart.repository.LanguageRepository;
import usi.si.seart.repository.TableRowCountRepository;
import usi.si.seart.repository.TaskRepository;
import usi.si.seart.views.TableRowCount;
import usi.si.seart.views.language.CountByLanguage;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface StatisticsService {

    Long codeSize();
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

        TableRowCountRepository tableRowCountRepository;

        GitRepoCountByLanguageRepository gitRepoCountByLanguageRepository;
        FileCountByLanguageRepository fileCountByLanguageRepository;
        FunctionCountByLanguageRepository functionCountByLanguageRepository;

        CodeRepository codeRepository;
        LanguageRepository languageRepository;
        TaskRepository taskRepository;

        @Override
        public Long codeSize() {
            return codeRepository.size();
        }

        @Override
        public Long countUsers() {
            return tableRowCountRepository.findById("user")
                    .map(TableRowCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Long countGitRepos() {
            return tableRowCountRepository.findById("git_repo")
                    .map(TableRowCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Long countFiles() {
            return tableRowCountRepository.findById("file")
                    .map(TableRowCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Long countFunctions() {
            return tableRowCountRepository.findById("function")
                    .map(TableRowCount::getCount)
                    .orElse(0L);
        }

        @Override
        public Map<Language, Long> countGitReposByLanguage() {
            return getLanguageCount(gitRepoCountByLanguageRepository::findAll);
        }

        @Override
        public Map<Language, Long> countFilesByLanguage() {
            return getLanguageCount(fileCountByLanguageRepository::findAll);
        }

        @Override
        public Map<Language, Long> countFunctionsByLanguage() {
            return getLanguageCount(functionCountByLanguageRepository::findAll);
        }

        private Map<Language, Long> getLanguageCount(Supplier<List<? extends CountByLanguage>> supplier) {
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
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2)
            );
        }
    }
}
