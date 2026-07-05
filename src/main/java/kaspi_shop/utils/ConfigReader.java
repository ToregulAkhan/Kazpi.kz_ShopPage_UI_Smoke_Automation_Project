package kaspi_shop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;

    private ConfigReader() {
    }

    private static void loadIfNeeded() {
        if (isLoaded) {
            return;
        }
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties не найден в resources");
            }
            properties.load(input);
            isLoaded = true;

        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать config.properties", e);
        }
    }

    public static String get(String key) {
        loadIfNeeded();
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Свойство '" + key + "' отсутствует в config.properties");
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        loadIfNeeded();
        return properties.getProperty(key, defaultValue);
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }
}

