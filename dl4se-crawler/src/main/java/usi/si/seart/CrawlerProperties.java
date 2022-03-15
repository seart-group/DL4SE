package usi.si.seart;

import lombok.experimental.UtilityClass;
import usi.si.seart.io.PropertiesReader;

import java.time.LocalDate;

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
}
