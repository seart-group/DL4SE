package usi.si.seart.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class PropertiesReaderTest {

    PropertiesReader propertiesReader = new PropertiesReader("reader.properties");

    static String user = System.getenv("DB_USER");
    static String pass = System.getProperty("db.password");
    static String srvr = System.getenv("DB_SERVER");
    static String port = System.getenv("DB_PORT");

    @Test
    void getPropertyTest() {

        Assertions.assertNull(propertiesReader.getProperty("app.property.nonexistant"));

        List<String> expected = List.of(
                "",
                "dl4se_properties",
                "this is a test for the properties parsing",
                "2022-01-01",
                user,
                pass,
                "jdbc:postgresql://"+srvr+"/"+port,
                "dl4se_properties",
                "The "+user+" is testing dl4se_properties with "+pass
        );

        List<String> properties = List.of(
                "app.property.empty",
                "app.property.name",
                "app.property.description",
                "app.property.date",
                "app.property.db.user",
                "app.property.db.password",
                "app.property.db.url",
                "app.property.name.copy",
                "app.property.multiple"
        );

        List<String> actual = properties.stream()
                .map(propertiesReader::getProperty)
                .collect(Collectors.toList());

        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Assertions.assertEquals(expected.get(i), actual.get(i));
        }
    }
}