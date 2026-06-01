package pages;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BasePage;
import utilities.ConfigReader;

public class RegistrationPage extends BasePage {

    // ── Locators ──────────────────────────────────────────

    private By firstNameField     = By.id("first_name");
    private By lastNameField      = By.id("last_name");
    private By emailField         = By.id("email");
    private By passwordField      = By.id("password");
    private By confirmPassField   = By.id("confirm_password");
    private By captchaLabel       = By.xpath(
        "//label[@for='captcha_answer']");
    private By captchaAnswerField = By.id("captcha_answer");
    private By termsCheckbox      = By.id("terms");
    private By createAccountBtn   = By.xpath(
        "//button[@type='submit' and contains(@class,'btn')]");
    private By successMessage = By.cssSelector(".alert-success p");

    // store generated credentials so they can be
    // reused in post-registration login attempt
    private String generatedEmail    = "";
    private String generatedPassword = "Test@1234";

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    // ── Getters for generated credentials ────────────────
    // StepDefinitions can access these after form is filled

    public String getGeneratedEmail() {
        return generatedEmail;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    // ── Random Email ──────────────────────────────────────
    // Each run generates a fresh unique email
    // so registration never fails with "already registered"

    public String generateRandomEmail() {
        int random = new Random().nextInt(99999);
        String email = "testuser" + random + "@gmail.com";
        System.out.println("Generated email: " + email);
        return email;
    }

    // ── Open Registration Page ────────────────────────────
    // Direct URL — skips the signup dropdown entirely

    public void openRegistrationPage() {
        navigateTo(ConfigReader.getBaseUrl() + "signup");
        dismissDemoPopup();
        // wait for first field to confirm page loaded
        wait.waitForVisible(firstNameField);
        System.out.println("Registration page opened.");
    }

    // ── Captcha Solver ────────────────────────────────────
    // Label text: "Security Check: What is four times two? *"
    // We read it, extract the math part, solve it, return answer

    private int solveCaptcha() {
        try {
            String labelText = driver
                .findElement(captchaLabel)
                .getText()
                .toLowerCase();

            System.out.println("Captcha label: " + labelText);

            // extract everything after "what is"
            String mathPart = "";
            if (labelText.contains("what is")) {
                mathPart = labelText
                    .substring(labelText.indexOf("what is") + 7)
                    .replace("?", "")
                    .replace("*", "")
                    .trim();
            }

            System.out.println("Math part: " + mathPart);

            // split into tokens: ["four", "times", "two"]
            String[] tokens = mathPart.trim().split("\\s+");

            if (tokens.length < 3) {
                System.out.println("Could not parse captcha.");
                return 0;
            }

            int num1   = parseNumber(tokens[0]);
            String op  = tokens[1].toLowerCase();
            int num2   = parseNumber(tokens[2]);
            int answer = calculate(num1, op, num2);

            System.out.println("Captcha solved: "
                + num1 + " " + op + " " + num2
                + " = " + answer);
            return answer;

        } catch (Exception e) {
            System.out.println("Captcha error: "
                + e.getMessage());
            return 0;
        }
    }

    private int calculate(int num1, String op, int num2) {
        switch (op) {
            case "plus":
            case "+":
            case "added":
                return num1 + num2;
            case "minus":
            case "-":
            case "subtract":
            case "subtracted":
            case "less":
                return num1 - num2;
            case "times":
            case "x":
            case "multiplied":
            case "multiply":
            case "*":
            case "×":
                return num1 * num2;
            case "divided":
            case "divide":
            case "/":
                if (num2 != 0) return num1 / num2;
                return 0;
            default:
                System.out.println("Unknown op: " + op);
                return 0;
        }
    }

    private int parseNumber(String word) {
        word = word.toLowerCase().trim()
            .replace(",", "")
            .replace(".", "");

        try {
            return Integer.parseInt(word);
        } catch (NumberFormatException e) {
            // not a digit, try word form below
        }

        switch (word) {
            case "zero":      return 0;
            case "one":       return 1;
            case "two":       return 2;
            case "three":     return 3;
            case "four":      return 4;
            case "five":      return 5;
            case "six":       return 6;
            case "seven":     return 7;
            case "eight":     return 8;
            case "nine":      return 9;
            case "ten":       return 10;
            case "eleven":    return 11;
            case "twelve":    return 12;
            case "thirteen":  return 13;
            case "fourteen":  return 14;
            case "fifteen":   return 15;
            case "sixteen":   return 16;
            case "seventeen": return 17;
            case "eighteen":  return 18;
            case "nineteen":  return 19;
            case "twenty":    return 20;
            case "thirty":    return 30;
            case "forty":     return 40;
            case "fifty":     return 50;
            case "sixty":     return 60;
            case "seventy":   return 70;
            case "eighty":    return 80;
            case "ninety":    return 90;
            case "hundred":   return 100;
            default:
                System.out.println("Unknown word: " + word);
                return 0;
        }
    }

    // ── Fill Form ─────────────────────────────────────────

    public void fillRegistrationForm(String firstName,
                                      String lastName) {
        // store generated email so post-registration
        // login attempt can use it
        generatedEmail = generateRandomEmail();

        // Step 1 — name fields
        type(firstNameField, firstName);
        System.out.println("First name entered: " + firstName);

        type(lastNameField, lastName);
        System.out.println("Last name entered: " + lastName);

        // Step 2 — email
        type(emailField, generatedEmail);
        System.out.println("Email entered: " + generatedEmail);

        // Step 3 — password via JavaScript
        // Alpine.js intercepts normal sendKeys for strength meter
        // JS setValue + triggerInput handles this correctly
        setValueByJS("password", generatedPassword);
        triggerInputEvent("password");
        System.out.println("Password entered.");

        // Step 4 — confirm password via JavaScript
        setValueByJS("confirm_password", generatedPassword);
        triggerInputEvent("confirm_password");
        System.out.println("Confirm password entered.");

        // Step 5 — solve and enter captcha answer
        int answer = solveCaptcha();
        type(captchaAnswerField, String.valueOf(answer));
        System.out.println("Captcha answered: " + answer);

        // Step 6 — check terms checkbox
        // JS click needed because real input is hidden behind
        // a custom styled div (.checkbox-custom)
        WebElement checkbox = driver.findElement(termsCheckbox);
        if (!checkbox.isSelected()) {
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", checkbox);
        }
        System.out.println("Terms checkbox checked.");
        System.out.println("All fields filled.");
    }

    // ── Submit Form ───────────────────────────────────────

    public void submitRegistration() {
        // JS click bypasses :disabled="isSubmitting"
        // Alpine.js binding on the Create Account button
        WebElement btn = driver.findElement(createAccountBtn);
        ((JavascriptExecutor) driver)
            .executeScript("arguments[0].click();", btn);
        System.out.println("Create Account button clicked.");
    }

    // ── JS Helpers ────────────────────────────────────────

    private void setValueByJS(String fieldId, String value) {
        ((JavascriptExecutor) driver).executeScript(
            "document.getElementById('" + fieldId
            + "').value = '" + value + "';",
            new Object[0]);
    }

    private void triggerInputEvent(String fieldId) {
        ((JavascriptExecutor) driver).executeScript(
            "var el = document.getElementById('"
            + fieldId + "');"
            + "var event = new Event('input',{bubbles:true});"
            + "el.dispatchEvent(event);",
            new Object[0]);
    }

    // ── isRegistrationSuccessful ──────────────────────────
    // Called BEFORE handlePostRegistration
    // Just checks that form submitted and redirected to login

    public boolean isRegistrationSuccessful() {
        try {
            // Wait for redirect to login page
            wait.waitForUrlContains("login");

            // Check if success message is displayed
            if (isDisplayed(successMessage)) {

                String message = getText(successMessage);

                System.out.println("Success Message: " + message);

                if (message.contains("Registration successful")) {
                    System.out.println(
                        "Registration completed successfully.");
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.out.println(
                "Registration success validation failed: "
                + e.getMessage());
            return false;
        }
    }
}