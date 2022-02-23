package usi.si.seart.parser;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.utils.HibernateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractParser implements Parser {

    protected static Language initializeLanguage(String languageName) {
        String queryFormat = "SELECT l FROM Language l WHERE l.name = '%s'";
        String queryString = String.format(queryFormat, languageName);
        SessionFactory sessionFactory = HibernateUtils.getFactory();
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(queryString, Language.class).getSingleResult();
        }
    }

    File.FileBuilder<?, ?> fileBuilder;
    List<Function.FunctionBuilder<?, ?>> functionBuilders;

    protected AbstractParser() {
        reset();
    }

    protected File buildAll() {
        File file = fileBuilder.build();
        List<Function> functions = functionBuilders.stream()
                .map(builder -> builder.file(file).isTest(file.getIsTest()).build())
                .collect(Collectors.toList());
        file.setFunctions(functions);
        reset();
        return file;
    }

    protected void reset() {
        fileBuilder = File.builder();
        functionBuilders = new ArrayList<>();
    }
}
