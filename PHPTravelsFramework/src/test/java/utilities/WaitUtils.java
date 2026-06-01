package utilities;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// WaitUtils contains all Explicit Wait methods
// NEVER use Thread.sleep() — always use these methods instead
//
// Why Explicit Wait is better than Thread.sleep:
// Thread.sleep(3000) → always waits 3 seconds even if element
//                       appeared in 0.5 seconds. Wastes time.
// waitForClickable()  → waits UP TO 15 seconds, stops as soon
//                       as element is ready. Much faster.

public class WaitUtils {

    private WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        // Duration.ofSeconds reads timeout from config
        int timeout = ConfigReader.getExplicitWait();
        this.wait = new WebDriverWait(driver,
            Duration.ofSeconds(timeout));
    }

    // waits until element is clickable (visible + enabled)
    // use before any .click() operation
    public WebElement waitForClickable(By locator) {
        return wait.until(
            ExpectedConditions.elementToBeClickable(locator));
    }

    // waits until element is visible on screen
    // use before reading text or checking display
    public WebElement waitForVisible(By locator) {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // waits until element exists in DOM
    // element may not be visible but exists in HTML
    public WebElement waitForPresence(By locator) {
        return wait.until(
            ExpectedConditions.presenceOfElementLocated(locator));
    }

    // waits until a JavaScript alert popup appears
    // use before driver.switchTo().alert()
    public Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    // waits until page switches to an iframe
    // use before interacting with elements inside iframes
    public void waitForFrame(By frameLocator) {
        wait.until(ExpectedConditions
            .frameToBeAvailableAndSwitchToIt(frameLocator));
    }

    // waits until URL contains expected text
    public void waitForUrlContains(String urlFragment) {
        wait.until(
            ExpectedConditions.urlContains(urlFragment));
    }

    // waits until page title matches expected
    public void waitForTitle(String title) {
        wait.until(ExpectedConditions.titleIs(title));
    }

    // waits until element disappears from screen
    public void waitForInvisibility(By locator) {
        wait.until(ExpectedConditions
            .invisibilityOfElementLocated(locator));
    }
}