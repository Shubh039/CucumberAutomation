package base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import utilities.WaitUtils;

// BasePage is the parent of ALL page classes
// Common actions live here so they are not repeated
// in every page class
//
// LoginPage    extends BasePage → gets all these methods
// SearchPage   extends BasePage → gets all these methods
// BookingPage  extends BasePage → gets all these methods

public class BasePage {

    protected WebDriver driver;
    protected WaitUtils wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
    }

	 // dismissDemoPopup closes the "I Understand & Continue" 
	 // popup that appears on every page load on phptravels.net
	 // Must be called before ANY action on the site
	 protected void dismissDemoPopup() {
	     try {
	         // wait for the popup button to appear
	         By continueBtn = By.xpath(
	             "//button[contains(text(),'I Understand') " +
	             "or contains(text(),'Continue') " +
	             "or contains(text(),'understand')]");
	         
	         WebDriverWait popupWait = new WebDriverWait(driver, 
	             Duration.ofSeconds(10));
	         WebElement btn = popupWait.until(
	             ExpectedConditions.elementToBeClickable(continueBtn));
	         btn.click();
	         System.out.println("Demo popup dismissed.");

	         // wait for popup to fully disappear before proceeding
	         popupWait.until(ExpectedConditions.invisibilityOfElementLocated(continueBtn));

     } catch (Exception e) {
         	// popup didn't appear — that's fine, continue
    	 	System.out.println("No popup found, continuing.");
     	}
	 }
    
    // ── Click ─────────────────────────────────────────────

    protected void click(By locator) {
        try {
            wait.waitForClickable(locator).click();
        } catch (StaleElementReferenceException e) {
            // StaleElement: element was refreshed in DOM
            // Retry once — this handles dynamic pages
            System.out.println("StaleElement on click. Retrying...");
            wait.waitForClickable(locator).click();
        }
    }

    // ── Type / Input ──────────────────────────────────────

    protected void type(By locator, String text) {
        try {
            WebElement field = wait.waitForVisible(locator);
            field.clear();
            field.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            System.out.println("StaleElement on type. Retrying...");
            WebElement field = wait.waitForVisible(locator);
            field.clear();
            field.sendKeys(text);
        }
    }

    // ── Read Text ─────────────────────────────────────────

    protected String getText(By locator) {
        try {
            return wait.waitForVisible(locator).getText().trim();
        } catch (TimeoutException e) {
            System.out.println("Timeout getting text: " + locator);
            return "";
        }
    }

    // ── Check Visibility ──────────────────────────────────

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ── Dropdown ──────────────────────────────────────────

    // selectByText handles <select> dropdown elements
    protected void selectByText(By locator, String text) {
        WebElement dropdown = wait.waitForVisible(locator);
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    protected void selectByValue(By locator, String value) {
        WebElement dropdown = wait.waitForVisible(locator);
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    // ── JavaScript ────────────────────────────────────────

    // jsClick bypasses overlays and intercepted clicks
    protected void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver)
            .executeScript("arguments[0].click();", element);
    }

    // scrollIntoView scrolls page to make element visible
    protected void scrollTo(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver)
            .executeScript(
                "arguments[0].scrollIntoView(true);", element);
    }

    // ── Navigation ────────────────────────────────────────

    protected void navigateTo(String url) {
        driver.get(url);
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        return driver.getTitle();
    }
}