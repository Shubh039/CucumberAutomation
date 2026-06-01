package runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

// SmokeTestRunner runs ONLY @Smoke scenarios
// Use this for quick build verification

@RunWith(Cucumber.class)
@CucumberOptions(
    features  = "src/test/resources/features/PHPtravelsfeature.feature",
    glue      = {"stepdefinitions", "hooks"},
    tags      = "@Smoke",
    plugin    = {
        "summary",
        "html:target/cucumber-reports/smoke-report.html",
        "json:target/cucumber-reports/smoke-report.json"
    },
    monochrome = true,
    dryRun     = false
)
public class SmokeTestRunner {
    // intentionally empty
}