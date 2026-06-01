package runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

// RegressionTestRunner runs ALL scenarios
// Use this before releases

@RunWith(Cucumber.class)
@CucumberOptions(
    features  = "src/test/resources/features/PHPtravelsfeature.feature",
    glue      = {"stepdefinitions", "hooks"},
    tags      = "@Regression or @Smoke",
    plugin    = {
        "summary",
        "html:target/cucumber-reports/regression-report.html",
        "json:target/cucumber-reports/regression-report.json",
        "junit:target/cucumber-reports/junit-report.xml"
    },
    monochrome = true,
    dryRun     = false
)
public class RegressionTestRunner {
    // intentionally empty
}