package usi.si.seart.io;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class PropertiesReaderTest {

    PropertiesReader propertiesReader = new PropertiesReader("reader.properties");

    static String user = "dabico";
    static String pass = "password123";
    static String srvr = "server.si.usi.ch";
    static String port = "12345";

    @BeforeAll
    static void beforeAll() {
        setEnvVar("DB_USER", user);
        setEnvVar("DB_SERVER", srvr);
        setEnvVar("DB_PORT", port);
        System.setProperty("db.password", pass);
    }

    // https://stackoverflow.com/a/40682052/17173324
    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static void setEnvVar(String key, String value) {
        Map<String, String> env = System.getenv();
        Class<?> cl = env.getClass();
        Field field = cl.getDeclaredField("m");
        field.setAccessible(true);
        Map<String, String> writableEnv = (Map<String, String>) field.get(env);
        writableEnv.put(key, value);
    }

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