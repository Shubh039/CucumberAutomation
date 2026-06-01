package utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

// ScreenshotUtils captures browser screenshots
// and saves them with timestamp in filename
// so screenshots never overwrite each other

public class ScreenshotUtils {

    // captureScreenshot saves screenshot to disk
    // and returns the file path (used by Extent Reports)
    public static String captureScreenshot(WebDriver driver,
                                           String scenarioName) {
        // timestamp format: 2026-05-25_14-30-45
        String timestamp = new SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss").format(new Date());

        // clean scenario name — remove spaces
        String cleanName = scenarioName
            .replaceAll("[^a-zA-Z0-9]", "_");

        // file path where screenshot will be saved
        String path = ConfigReader.get("screenshotPath")
            + cleanName + "_" + timestamp + ".png";

        try {
            // TakesScreenshot is Selenium interface for screenshots
            File srcFile = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.FILE);

            // create directory if it doesn't exist
            File destFile = new File(path);
            destFile.getParentFile().mkdirs();

            // copy screenshot file to our target path
            FileUtils.copyFile(srcFile, destFile);

            System.out.println("Screenshot saved: " + path);
            return path;

        } catch (IOException e) {
            System.out.println("Screenshot failed: "
                + e.getMessage());
            return null;
        }
    }

    // captureAsBytes returns screenshot as byte array
    // needed to attach screenshot directly to Cucumber report
    public static byte[] captureAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver)
            .getScreenshotAs(OutputType.BYTES);
    }
}