package usi.si.seart.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import usi.si.seart.CrawlerProperties;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.model.job.Job;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class HibernateUtils {

    @Getter
    private static final SessionFactory factory = new Configuration().configure().buildSessionFactory();

    public static CrawlJob getLastJob() {
        try (Session session = HibernateUtils.getFactory().openSession()) {
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

    public static Set<Language> getLanguages() {
        try (Session session = HibernateUtils.getFactory().openSession()) {
            return session.createQuery("SELECT l FROM Language l", Language.class)
                    .stream()
                    .collect(Collectors.toSet());
        }
    }

    public static void saveRepo(GitRepo repo) {
        Session session = factory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            log.debug("Saving: {}", repo);
            session.saveOrUpdate(repo);
            session.flush();
            transaction.commit();
            log.debug("Saved: {}", repo.getName());
        } catch (PropertyValueException ex) {
            log.error("Error while persisting: {}", repo);
            log.error("Error stack trace:", ex);
            session.flush();
            transaction.rollback();
        } finally {
            session.close();
        }
    }
}
