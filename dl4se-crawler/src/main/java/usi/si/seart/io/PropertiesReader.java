package usi.si.seart.io;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used for reading {@code Properties} files. It has the added benefit of being able to resolve references to
 * other file properties, system properties and environment variables, with the resolution precedence maintaining the
 * aforementioned order. The property values themselves remain preserved as they were written in the file, with the
 * resolution taking place once {@link #getProperty} is called. Only <code>${...}</code> is supported for property
 * referencing. Nested resolution (i.e. <code>${...${...}...}</code>) is not supported.
 *
 * @author dabico
 * @see System#getProperty(String) System.getProperty
 * @see System#getenv(String) System.getenv
 * @see Properties
 * @see Matcher
 * @see <a href="https://www.debuggex.com/r/Fc9Og5dvmOd_Dq_p">Pattern Definition</a>
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PropertiesReader {

    static Pattern referencePattern = Pattern.compile("\\$\\{([^}]+)\\}");

    Properties properties;
    Function<String, String> resolver;

    @SneakyThrows
    public PropertiesReader(String fileName) {
        this.properties = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        this.properties.load(is);

        UnaryOperator<String> resolver1 = resolver(this.properties::getProperty);
        UnaryOperator<String> resolver2 = resolver(System::getProperty);
        UnaryOperator<String> resolver3 = resolver(System::getenv);
        resolver = resolver1.andThen(resolver2).andThen(resolver3);
    }

    /**
     * Searches for the property with the specified key in this property list, performing resolutions if the property
     * value contains references to other properties or variables. The method returns null if the property is not found.
     *
     * @param key The property key.
     * @return The resolved property value.
     */
    public String getProperty(String key) {
        String value = this.properties.getProperty(key);
        if (value != null) return resolver.apply(value);
        else return null;
    }

    private UnaryOperator<String> resolver(UnaryOperator<String> mapper) {
        return str -> {
            Matcher matcher = referencePattern.matcher(str);

            if (str.matches(".*"+referencePattern.pattern()+".*")) {
                StringBuilder builder = new StringBuilder();

                while (matcher.find()) {
                    String name = matcher.group(1);
                    String value = mapper.apply(name);
                    if (value != null) matcher.appendReplacement(builder, Matcher.quoteReplacement(value));
                }

                matcher.appendTail(builder);
                return builder.toString();
            }

            return str;
        };
    }
}
