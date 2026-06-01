package reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import utilities.ConfigReader;

// ExtentReportManager creates the HTML report file
// Singleton pattern — only ONE instance created
// (getInstance returns same object every call)

public class ExtentReportManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            // timestamp in report filename
            String timestamp = new SimpleDateFormat(
                "yyyy-MM-dd_HH-mm-ss").format(new Date());

            String reportPath = ConfigReader
                .get("extentReportPath")
                + "Report_" + timestamp + ".html";

            // ExtentSparkReporter creates the visual HTML report
            ExtentSparkReporter spark =
                new ExtentSparkReporter(reportPath);

            // configure report appearance
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle(
                "PHPTravels Test Report");
            spark.config().setReportName(
                "Automation Test Results");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // system info shown in report header
            extent.setSystemInfo("Application",
                "PHPTravels.net");
            extent.setSystemInfo("Browser",
                ConfigReader.getBrowser());
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Tester", "Automation");

            System.out.println("Extent Report: " + reportPath);
        }
        return extent;
    }
}