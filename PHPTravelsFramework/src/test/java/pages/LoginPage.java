package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utilities.ConfigReader;

import java.time.Duration;

public class LoginPage extends BasePage {

    // ── Locators — from exact HTML provided ───────────────
    // id="email"    → email input
    // id="password" → password input
    // button[type="submit"] with :disabled="isSubmitting"
    //   → needs JS click to bypass Alpine.js disabled binding

    private By emailField    = By.id("email");
    private By passwordField = By.id("password");
    private By loginButton   = By.cssSelector(
        "button[type='submit']");
    private By errorAlert    = By.className("alert-error");
    private By pageLoader    = By.id("page-loader");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ── Open Login Page ───────────────────────────────────

    public void openLoginPage() {
        navigateTo(ConfigReader.getBaseUrl() + "login");

        // wait for page loader to disappear first
        // PHPTravels shows a loader div on every page load
        // clicking before it disappears causes interception
        waitForLoaderToDisappear();

        // dismiss demo popup if it appears
        dismissDemoPopup();

        // wait for email field to confirm page is ready
        wait.waitForVisible(emailField);

        System.out.println("Login page opened.");
    }

    // waitForLoaderToDisappear waits for the page-loader
    // div to become invisible before any action
    // This is the ROOT CAUSE of ElementClickInterceptedException
    // The loader <div id="page-loader"> sits on top of everything
    // during page transitions

    private void waitForLoaderToDisappear() {
        try {
            WebDriverWait loaderWait = new WebDriverWait(
                driver, Duration.ofSeconds(10));
            loaderWait.until(ExpectedConditions
                .invisibilityOfElementLocated(pageLoader));
            System.out.println("Page loader gone.");
        } catch (Exception e) {
            // loader not found or already gone — continue
        }
    }

    // ── Field Actions ─────────────────────────────────────

    public void enterEmail(String email) {
        type(emailField, email);
        System.out.println("Email entered: " + email);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
        System.out.println("Password entered.");
    }

    // ── Click Login Button ────────────────────────────────
    // Uses JavaScript click because:
    // 1. Button has :disabled="isSubmitting" Alpine.js binding
    // 2. page-loader div can intercept normal clicks
    // JS click bypasses both of these issues

    public void clickLoginButton() {
        try {
            // wait for loader to be gone before clicking
            waitForLoaderToDisappear();

            WebElement btn = driver.findElement(loginButton);
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", btn);
            System.out.println("Login button clicked.");
        } catch (Exception e) {
            System.out.println("Login button error: "
                + e.getMessage());
        }
    }

    // loginWith — single method for full login flow
    public void loginWith(String email, String password) {
        if (email != null && !email.isEmpty()) {
            enterEmail(email);
        }
        if (password != null && !password.isEmpty()) {
            enterPassword(password);
        }
        clickLoginButton();
    }

    // ── Validations ───────────────────────────────────────

    public boolean isLoginSuccessful() {
        try {
            // wait up to 10 seconds for URL to change
            // away from login page
            WebDriverWait urlWait = new WebDriverWait(
                driver, Duration.ofSeconds(10));

            // success = URL no longer contains "login"
            // PHPTravels redirects to /account or /dashboard
            urlWait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login")));

            String url = driver.getCurrentUrl();
            System.out.println("After login URL: " + url);
            return !url.contains("login");

        } catch (Exception e) {
            // still on login page = login failed
            return false;
        }
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorAlert);
    }

    public String getErrorMessage() {
        return getText(errorAlert);
    }

    public void verifyLoginSuccess() {
        boolean success = isLoginSuccessful();
        if (success) {
            System.out.println(
                "Tutorials Ninja, successfully Login done!");
        } else {
            System.out.println("Login verification failed.");
        }
    }
}