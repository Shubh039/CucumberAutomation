package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import utilities.ConfigReader;

// DriverFactory manages WebDriver creation
//
// ThreadLocal<WebDriver> means each thread gets its OWN
// separate driver instance. This is required for
// parallel execution — without it, multiple threads
// would fight over the same browser window.
//
// Think of ThreadLocal like a locker:
// Each thread has its own locker with its own driver inside.
// Thread 1 → its own Chrome window
// Thread 2 → its own Chrome window (separate)
public class DriverFactory {

    // ThreadLocal stores one WebDriver per thread
    // static so it's shared across all instances
    private static ThreadLocal<WebDriver> driver
        = new ThreadLocal<>();

    // createDriver reads browser from config.properties
    // and creates the appropriate driver
    public static void createDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        boolean headless = ConfigReader.isHeadless();
        WebDriver webDriver;

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
                System.out.println("Firefox browser launched.");
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                webDriver = new EdgeDriver();
                System.out.println("Edge browser launched.");
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--remote-allow-origins=*");
                    System.out.println("Chrome browser launched in headless mode.");
                } else {
                    System.out.println("Chrome browser launched.");
                }
                webDriver = new ChromeDriver(options);
                break;
        }

        webDriver.manage().window().maximize();

        // store this driver for the current thread
        driver.set(webDriver);
    }

    // getDriver returns the driver for current thread
    // called by all page objects and step definitions
    public static WebDriver getDriver() {
        return driver.get();
    }

    // quitDriver closes browser and removes from ThreadLocal
    // must be called after each scenario to clean up
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove(); // important — prevents memory leak
            System.out.println("Browser closed.");
        }
    }
}