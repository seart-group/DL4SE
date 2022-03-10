package usi.si.seart.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import usi.si.seart.CrawlerProperties;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.model.job.Job;

import javax.persistence.PersistenceException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
@SuppressWarnings("TryFinallyCanBeTryWithResources")
public class HibernateUtils {

    private static final SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public CrawlJob getLastJob() {
        try (Session session = factory.openSession()) {
            Optional<CrawlJob> lastJobOptional = session.createQuery(
                    "SELECT c FROM CrawlJob c WHERE c.jobType = usi.si.seart.model.job.Job.CODE", CrawlJob.class
            ).uniqueResultOptional();

            if (lastJobOptional.isPresent()) {
                return lastJobOptional.get();
            } else {
                CrawlJob startJob = CrawlJob.builder()
                        .checkpoint(CrawlerProperties.startDate.atStartOfDay())
                        .jobType(Job.CODE)
                        .build();
                save(startJob);
                return getLastJob();
            }
        }
    }

    public Set<Language> getLanguages() {
        try (Session session = factory.openSession()) {
            return session.createQuery("SELECT l FROM Language l", Language.class)
                    .stream()
                    .collect(Collectors.toSet());
        }
    }

    public Optional<GitRepo> getRepoByName(String name) {
        try (Session session = factory.openSession()) {
            Optional<GitRepo> result = session.createQuery("SELECT r FROM GitRepo r WHERE r.name = :name", GitRepo.class)
                    .setParameter("name", name)
                    .uniqueResultOptional();
            return result.map(repo -> {
                Hibernate.initialize(repo.getLanguages());
                return repo;
            });
        }
    }

    public void save(CrawlJob crawlJob) {
        saveOrUpdate(crawlJob);
    }

    public void save(GitRepo repo) {
        saveOrUpdate(repo);
    }

    public void save(File file) {
        saveOrUpdate(file);
    }

    private void saveOrUpdate(Object obj) {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(obj);
            session.flush();
            transaction.commit();
        } catch (PersistenceException ex) {
            log.error("Error while persisting: " + obj, ex);
            if (transaction != null) transaction.rollback();
        } finally {
            session.close();
        }
    }

    public void deleteRepoById(Long id) {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM GitRepo WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            session.flush();
            transaction.commit();
        } catch (PersistenceException ex) {
            log.error("Exception occurred while deleting GitRepo["+id+"]!", ex);
            if (transaction != null) transaction.rollback();
        } finally {
            session.close();
        }
    }

    public void deleteFileByRepoIdAndPath(Long id, Path path) {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM File WHERE repo.id = :id AND path = :path")
                    .setParameter("id", id)
                    .setParameter("path", path.toString())
                    .executeUpdate();
            session.flush();
            transaction.commit();
        } catch (PersistenceException ex) {
            log.error("Exception occurred while deleting File["+id+"]!", ex);
            if (transaction != null) transaction.rollback();
        } finally {
            session.close();
        }
    }

    public void updateFilePathByRepoId(Long id, Path before, Path after) {
        Session session = factory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createQuery("UPDATE File SET path = :after WHERE repo.id = :id AND path = :before")
                    .setParameter("id", id)
                    .setParameter("before", before.toString())
                    .setParameter("after", after.toString())
                    .executeUpdate();
            session.flush();
            transaction.commit();
        } catch (PersistenceException ex) {
            log.error("Exception occurred while updating File["+id+"]!", ex);
            if (transaction != null) transaction.rollback();
        } finally {
            session.close();
        }
    }
}
