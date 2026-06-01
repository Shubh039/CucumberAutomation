package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// ConfigReader loads config.properties file
// so any class can get settings like browser name,
// URL, timeouts without hardcoding them in Java

public class ConfigReader {

    private static Properties properties;

    // static block runs ONCE when class is first loaded
    // loads the properties file into memory
    static {
        try {
            FileInputStream fis = new FileInputStream(
                "src/test/resources/config/config.properties");
            properties = new Properties();
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.out.println("ERROR: config.properties not found!");
            e.printStackTrace();
        }
    }

    // get() returns value for any key from properties file
    public static String get(String key) {
        return properties.getProperty(key);
    }

    // convenience methods for common settings
    public static String getBrowser() {
        return properties.getProperty("browser", "chrome");
    }

    public static String getBaseUrl() {
        return properties.getProperty("baseUrl");
    }

    public static int getExplicitWait() {
        return Integer.parseInt(
            properties.getProperty("explicitWait", "15"));
    }
}