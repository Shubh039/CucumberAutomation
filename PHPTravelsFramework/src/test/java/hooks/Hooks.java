package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import base.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import reports.ExtentReportManager;
import utilities.ScreenshotUtils;

public class Hooks {

    private static ExtentReports extent =
        ExtentReportManager.getInstance();

    // ThreadLocal so each thread has its own test log entry
    private static ThreadLocal<ExtentTest> extentTest =
        new ThreadLocal<>();

    // ── Before Each Scenario ──────────────────────────────

    @Before
    public void setup(Scenario scenario) {
        // Create browser for this scenario
        DriverFactory.createDriver();

        // Create a new test entry in Extent Report
        ExtentTest test = extent.createTest(scenario.getName());
        extentTest.set(test);

        test.log(Status.INFO,
            "Scenario started: " + scenario.getName());
        System.out.println("=== Scenario: "
            + scenario.getName() + " ===");
    }

    // ── After Each Step ───────────────────────────────────
    // Screenshots on PASS and FAIL as required

    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            // screenshot on FAILURE — attached to report
            byte[] screenshot = ScreenshotUtils
                .captureAsBytes(DriverFactory.getDriver());
            scenario.attach(screenshot, "image/png",
                "Failure Screenshot");

            // also log in Extent Report
            String path = ScreenshotUtils.captureScreenshot(
                DriverFactory.getDriver(), scenario.getName());
            if (extentTest.get() != null && path != null) {
                extentTest.get().log(Status.FAIL,
                    "Step failed. Screenshot: " + path);
            }
        }
    }

    // ── After Each Scenario ───────────────────────────────

    @After
    public void teardown(Scenario scenario) {
        ExtentTest test = extentTest.get();

        if (scenario.isFailed()) {
            // screenshot on FAILURE
            byte[] screenshot = ScreenshotUtils
                .captureAsBytes(DriverFactory.getDriver());
            scenario.attach(screenshot, "image/png",
                "Final Screenshot");

            if (test != null) {
                test.log(Status.FAIL,
                    "Scenario FAILED: " + scenario.getName());
            }
            System.out.println("FAILED: " + scenario.getName());

        } else if (scenario.getStatus().toString()
                .equals("SKIPPED")) {
            // screenshot on SKIP
            if (test != null) {
                test.log(Status.SKIP,
                    "Scenario SKIPPED: " + scenario.getName());
            }
            System.out.println("SKIPPED: " + scenario.getName());

        } else {
            // screenshot on PASS
            String path = ScreenshotUtils.captureScreenshot(
                DriverFactory.getDriver(), scenario.getName());
            if (test != null) {
                test.log(Status.PASS,
                    "Scenario PASSED: " + scenario.getName());
            }
            System.out.println("PASSED: " + scenario.getName());
        }

        // flush saves the Extent Report to file
        extent.flush();

        // close browser for this thread
        DriverFactory.quitDriver();
    }
}