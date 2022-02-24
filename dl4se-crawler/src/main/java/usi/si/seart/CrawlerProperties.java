package usi.si.seart;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Properties;

@UtilityClass
public class CrawlerProperties {

    public static final String tmpDirPrefix;
    public static final LocalDate startDate;
    public static final String ghsSearchUrl;

    static {
        PropertiesReader propertiesReader = new PropertiesReader("application.properties");
        tmpDirPrefix = propertiesReader.getProperty("app.general.tmpDirPrefix");
        String dateString = propertiesReader.getProperty("app.crawl.startDate");
        startDate = LocalDate.parse(dateString);
        ghsSearchUrl = propertiesReader.getProperty("app.crawl.ghs.searchUrl");
    }

    private static class PropertiesReader {
        private final Properties properties;

        @SneakyThrows
        public PropertiesReader(String propertyFileName) {
            InputStream is = getClass().getClassLoader().getResourceAsStream(propertyFileName);
            this.properties = new Properties();
            this.properties.load(is);
        }

        public String getProperty(String propertyName) {
            return this.properties.getProperty(propertyName);
        }
    }
}
