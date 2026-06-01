package stepdefinitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.DriverFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.HotelSearchPage;
import pages.LoginPage;
import pages.RegistrationPage;
import utilities.ConfigReader;
import utilities.ExcelUtils;

public class StepDefinitions {

	// ── Page object helpers ───────────────────────────────
	// Always get fresh instance with current thread's driver
	// Thread safe because DriverFactory uses ThreadLocal
	
	private WebDriver getDriver() {
	    return DriverFactory.getDriver();
	}
	
	private LoginPage getLoginPage() {
	    return new LoginPage(getDriver());
	}
	
	private RegistrationPage getRegPage() {
	    return new RegistrationPage(getDriver());
	}
	
	private HotelSearchPage openPage() {
		return new HotelSearchPage(getDriver());
	}
	// ═══════════════════════════════════════════════════════
	// SCENARIO: Validate Login Functionality
	// Runs 4 times via Scenario Outline — one per Example row
	// ═══════════════════════════════════════════════════════
	
	@Given("user launches browser")
	public void launchBrowser() {
	    // driver already created in Hooks @Before
	    // nothing to do here
	    System.out.println("Browser launched.");
	}
	
	@When("user enters {string} and {string}")
	public void enterCredentials(String username,
	                              String password) {
	    // open login page — popup dismissed inside
	    getLoginPage().openLoginPage();
	
	    // only type if not empty
	    // empty string = testing blank field scenario
	    if (!username.isEmpty()) {
	        getLoginPage().enterEmail(username);
	    } else {
	        System.out.println("Username left empty — testing blank field.");
	    }
	
	    if (!password.isEmpty()) {
	        getLoginPage().enterPassword(password);
	    } else {
	        System.out.println("Password left empty — testing blank field.");
	    }
	}
	
	@And("clicks on login button")
	public void clickLoginButton() {
	    // JS click inside LoginPage handles Alpine.js
	    // and page-loader interception
	    getLoginPage().clickLoginButton();
	}
	
	@Then("validate login result {string}")
	public void validateLoginResult(String expectedResult) {
	    if (expectedResult.equalsIgnoreCase("success")) {
	
	        // ── Success case ──────────────────────────────
	        // URL should move away from /login
	        boolean success = getLoginPage().isLoginSuccessful();
	        Assert.assertTrue(
	            "Expected login SUCCESS but login FAILED. "
	            + "Check credentials in Examples table.",
	            success);
	        System.out.println("Login successful - PASSED");
	
	    } else {
	
	        // ── Failure case ──────────────────────────────
	        // URL should stay on /login OR error should show
	        // Both indicate login was correctly rejected
	        boolean loginFailed =
	            !getLoginPage().isLoginSuccessful();
	        boolean errorShown =
	            getLoginPage().isErrorDisplayed();
	
	        // either condition proves login was blocked
	        Assert.assertTrue(
	            "Expected login FAILURE but login SUCCEEDED. "
	            + "Check credentials in Examples table.",
	            loginFailed || errorShown);
	        System.out.println("Login failure verified - PASSED");
	    }
	}
	    
	 // ═══════════════════════════════════════════════════════
	 // SCENARIO: Validate Login Functionality Using Excel
	 // ═══════════════════════════════════════════════════════
	
	@When("user performs login using excel data")
	public void loginUsingExcel() {
	
	    ExcelUtils excel = new ExcelUtils(
	        ConfigReader.get("excelPath"),
	        ConfigReader.get("excelSheet"));
	
	    int rowCount = excel.getRowCount();
	    System.out.println("Total data rows in Excel: " + rowCount);
	
	    for (int i = 1; i <= rowCount; i++) {
	
	        String username       = excel.getCellData(i, 0);
	        String password       = excel.getCellData(i, 1);
	        String expectedResult = excel.getCellData(i, 2);
	
	        System.out.println("--- Excel Row " + i + " ---");
	        System.out.println("Username : " + username);
	        System.out.println("Password : "
	            + (password.isEmpty() ? "(empty)" : "****"));
	        System.out.println("Expected : " + expectedResult);
	
	        // ── Step 1: Open login page ────────────────────────
	        getLoginPage().openLoginPage();
	
	        // ── Step 2: Enter credentials ──────────────────────
	        if (!username.isEmpty()) {
	            getLoginPage().enterEmail(username);
	        } else {
	            System.out.println("Row " + i
	                + ": Username left blank.");
	        }
	
	        if (!password.isEmpty()) {
	            getLoginPage().enterPassword(password);
	        } else {
	            System.out.println("Row " + i
	                + ": Password left blank.");
	        }
	
	        // ── Step 3: Click login ────────────────────────────
	        getLoginPage().clickLoginButton();
	
	        // ── Step 4: Verify result ──────────────────────────
	        boolean loggedIn = getLoginPage().isLoginSuccessful();
	
	        if (expectedResult.equalsIgnoreCase("success")) {
	            if (loggedIn) {
	                System.out.println("Row " + i
	                    + " → LOGIN SUCCESS ✓");
	            } else {
	                System.out.println("Row " + i
	                    + " → LOGIN FAILED ✗ — check Excel data");
	            }
	        } else {
	            if (!loggedIn) {
	                System.out.println("Row " + i
	                    + " → FAILURE VERIFIED ✓");
	            } else {
	                System.out.println("Row " + i
	                    + " → UNEXPECTED SUCCESS ✗");
	            }
	        }
	
	        // ── Step 5: Clear session at end of each row ───────
	        // Wipes all cookies and storage so next row
	        // starts with zero session state.
	        // Much faster than closing and reopening browser.
	        // Works because PHPTravels uses cookie-based sessions
	        // and Alpine.js stores state in localStorage.
	        try {
	            // destroy server-side session cookie
	            getDriver().manage().deleteAllCookies();
	
	            // clear Alpine.js and any JWT tokens
	            JavascriptExecutor js =
	                (JavascriptExecutor) getDriver();
	            js.executeScript("window.localStorage.clear();");
	            js.executeScript("window.sessionStorage.clear();");
	
	            System.out.println("Row " + i
	                + " → Session cleared successfully.");
	
	        } catch (Exception e) {
	            System.out.println("Warning: Failed to clear"
	                + " session state: " + e.getMessage());
	        }
	
	        System.out.println("Row " + i + " complete.");
	        System.out.println("─────────────────────────────");
	    }
	
	    excel.close();
	    System.out.println("Excel file closed.");
	    System.out.println("All Excel rows processed - DONE");
	}
	
	@Then("excel login validation should complete")
	public void excelLoginValidation() {
	    System.out.println("==================================");
	    System.out.println("Excel Login Validation Completed");
	    System.out.println("All Excel rows processed.");
	    System.out.println("==================================");
	 }
	
	 // ═══════════════════════════════════════════════════════
	 // SCENARIO: Validate User Registration
	 // Feature:
	 //   Given user opens registration page
	 //   When user enters all mandatory registration details
	 //   And user submits the registration form
	 //   Then registration should be successful
	 //   And post registration flow should be handled
	 // ═══════════════════════════════════════════════════════
	
	 @Given("user opens registration page")
	 public void openRegistration() {
	     getRegPage().openRegistrationPage();
	     System.out.println(
	         "========== Registration page opened ==========");
	 }
	
	 @When("user enters all mandatory registration details")
	 public void enterDetails() {
	     // fillRegistrationForm fills all fields:
	     // first name, last name, email (random generated),
	     // password via JS, confirm password via JS,
	     // captcha solved automatically, terms checkbox checked
	     getRegPage().fillRegistrationForm(
	         "Test",   // firstName
	         "User"   // lastName
	         );
	     System.out.println(
	         "========== All registration details entered ==========");
	 }
	
	 @And("user submits the registration form")
	 public void submitRegistration() {
	     // JS click bypasses Alpine.js :disabled binding
	     getRegPage().submitRegistration();
	     System.out.println(
	         "========== Registration form submitted ==========");
	 }
	
	 @Then("registration should be successful")
	 public void verifyRegistration() {
	     // Step 1 — verify redirect to login page happened
	     // redirect to /login = account was created in DB
	     boolean success = getRegPage().isRegistrationSuccessful();
	     Assert.assertTrue(
	         "Registration FAILED — page did not redirect to login.",
	         success);
	     System.out.println(
	         "========== Registration successfull ==========");
	 }
	
	 
	 // ═══════════════════════════════════════════════════════
	 // HOTEL SEARCH STEPS
	 // Feature: hotel_search.feature
	 // ═══════════════════════════════════════════════════════
	
	 @Given("user is on PHPTravels home page")
	 public void openHotelSearch() {
		 openPage().openHomePage();
	 }
	
	 @When("user clicks on Stays tab")
	 public void clickStaysTab() {
	     new HotelSearchPage(getDriver()).clickStaysTab();
	 }
	 
	 @When("user enters hotel destination {string}")
	 public void enterHotelDestination(String destination) {
	     new HotelSearchPage(getDriver())
	        .enterDestination(destination);
	 }
	
	
	 @And("user selects checkin date {string}")
	 public void selectCheckinDate(String date) {
	     new HotelSearchPage(getDriver())
	        .selectCheckInDate(date);
	 }
	
	 @And("user selects checkout date {string}")
	 public void selectCheckoutDate(String date) {
	     new HotelSearchPage(getDriver())
	         .selectCheckOutDate(date);
	 }
	 
	 @And("user selects nationality {string}")
	 public void selectNationality(String nation) {
	     new HotelSearchPage(getDriver())
	         .enterNationality(nation);
	 }
	
	 @And("user clicks hotel search button")
	public void clickHotelSearchButton() {
	    new HotelSearchPage(getDriver()).clickSearchButton();
	}
	
	@Then("hotel search results should be displayed")
	public void verifyHotelSearchResults() {
	    HotelSearchPage search =
	        new HotelSearchPage(getDriver());
	    boolean displayed = search.isResultsDisplayed();
	    Assert.assertTrue(
	        "Hotel search results not displayed.", displayed);
	    System.out.println("Hotel results displayed - PASSED");
	}
	
	@And("available hotels count should be shown")
	public void verifyHotelCount() {
	    HotelSearchPage search =
	        new HotelSearchPage(getDriver());
	    int count = search.getHotelCount();
	    Assert.assertTrue(
	        "No hotels found in results.", count > 0);
	    System.out.println("Available hotels: "
	        + count + " - PASSED");
	}
}
