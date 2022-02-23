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
import usi.si.seart.model.GitRepo;

@Slf4j
@UtilityClass
public class HibernateUtils {

    @Getter
    private static SessionFactory factory;

    static {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (HibernateException ex) {
            log.error("Error creating the session factory:", ex);
            log.error("Aborting...");
            System.exit(1);
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
