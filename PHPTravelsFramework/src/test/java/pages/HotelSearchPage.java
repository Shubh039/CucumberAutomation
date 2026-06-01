package pages;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;
import utilities.ConfigReader;

public class HotelSearchPage extends BasePage {

    // ── Locators — from exact HTML provided ───────────────

    // Stays tab button — must click this first
    // @click="activeTab = '3'" means it activates stays form
    private By staysTab = By.xpath(
    	    "//button[.//span[text()='hotel'] and .//span[text()='Stays']]"
    		);

    // Destination input — id="destination_input"
    // x-model="destinationSearch" with debounce
    private By destinationInput = By.id("destination_input");

    // Autocomplete suggestion dropdown results
    // appears after typing 2+ characters
//    private By destinationSuggestions = By.xpath(
//        "//div[contains(@class,'input-dropdown-content')" +
//        " and contains(@class,'show')]//div[contains(@class,'p-2')]" +
//        " | //div[@class='absolute left-0 right-0 z-50']" +
//        "//div[contains(@class,'p-2.5')]");

    // Check-in date input — name="checkin_date"
    // readonly field — must use JS to set value
//    private By checkInInput = By.cssSelector(
//        "input[name='checkin_date']");

    // Check-out date input — name="checkout_date"
    // readonly field — must use JS to set value
//    private By checkOutInput = By.cssSelector(
//        "input[name='checkout_date']");

    private By nationalityDropdownBtn =
    	    By.xpath("//div[contains(@class,'field-box')][.//span[text()='Nationality']]");
    private By nationalitySearchInput =
    	    By.cssSelector("input[x-ref='nationalitySearch']");
    
    // Search button — type="submit" with :disabled="isSearching"
    private By searchButton = By.xpath(
        "//button[@type='submit' " +
        "and @title='Search Hotels']");

    // Results count — "8 stays" text
    // x-text="filteredHotels.length + ' stay' + ..."
    private By hotelCountSpan = By.xpath(
        "//span[@x-show=\"!loading\" " +
        "or contains(@x-text,'filteredHotels')]" +
        " | //h2//span[contains(text(),'stay')]");

    // Hotel cards on results page
//    private By hotelCards = By.cssSelector(
//        ".hotel-card-animate, " +
//        "div[x-html*='renderHotelCard']");

    // Hotel prices on results page
    // "USD 1545.00" pattern
//    private By hotelPrices = By.xpath(
//        "//p[contains(@class,'text-2xl') " +
//        "and contains(@class,'font-bold')]");

    // Hotel names on results page
//    private By hotelNames = By.xpath(
//        "//h3[contains(@class,'font-bold') " +
//        "and contains(@class,'text-lg')]");

    // Page loader — must wait for this to disappear
    private By pageLoader = By.id("page-loader");

    public HotelSearchPage(WebDriver driver) {
        super(driver);
    }

    // ── Open Home Page ────────────────────────────────────

    public void openHomePage() {
        navigateTo(ConfigReader.getBaseUrl());
        waitForPageLoader();
        dismissDemoPopup();
        System.out.println("PHPTravels home page opened.");
    }

    // ── Wait for page loader to disappear ─────────────────
    // PHPTravels shows a loading spinner on every page load
    // We must wait for it to disappear before interacting

    public void waitForPageLoader() {
        try {
            WebDriverWait loaderWait = new WebDriverWait(
                driver, Duration.ofSeconds(15));
            loaderWait.until(ExpectedConditions
                .invisibilityOfElementLocated(pageLoader));
        } catch (Exception e) {
            // loader not present, continue
        }
    }

    // ── Click Stays Tab ───────────────────────────────────
    // The home page has multiple tabs: Flights, Stays, Cars etc
    // We must click "Stays" tab to show the hotel search form
    // Button has @click="activeTab = '3'" Alpine.js handler

    public void clickStaysTab() {
        try {
            // Try by span text content first
            WebElement tab = wait.waitForClickable(
                staysTab);
            tab.click();
            System.out.println("Stays tab clicked.");

            // Wait for hotel search form to appear
            wait.waitForVisible(destinationInput);
            System.out.println("Hotel search form visible.");

        } catch (Exception e) {
            // Fallback — find by hotel icon + Stays text
            try {
                WebElement tab = driver.findElement(
                    staysTab);
                ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].click();", tab);
                System.out.println(
                    "Stays tab clicked via JS.");
            } catch (Exception e2) {
                System.out.println(
                    "Stays tab error: " + e2.getMessage());
            }
        }
    }

    // ── Enter Destination ─────────────────────────────────
    // The destination field uses Alpine.js x-model
    // It triggers fetchDestination() after 300ms debounce
    // An autocomplete dropdown appears with city suggestions
    // We must click on a suggestion to properly select it
    // (just typing is NOT enough — the form needs destinationSelected)

    public void enterDestination(String destination) {
        try {
            // Click the destination input to focus it
            WebElement input = wait.waitForClickable(
                destinationInput);
            input.click();
            input.clear();
            input.sendKeys(destination);
            System.out.println("Destination typed: "
                + destination);

        } catch (Exception e) {
            System.out.println("Destination error: "
                + e.getMessage());
        }
    }

    // ── Select Check-in Date ──────────────────────────────
    // The check-in input is readonly (has a date picker)
    // We use JavaScript to set the value directly
    // Format must match: DD-MM-YYYY (e.g. 10-06-2026)

    public void selectCheckInDate(String date) {
        try {
            // Use JS to set value on readonly field
            ((JavascriptExecutor) driver).executeScript(
                "document.querySelector(" +
                "'input[name=\"checkin_date\"]')" +
                ".value = arguments[0];", date);

            // Also trigger change event so Alpine.js detects it
            ((JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector(" +
                "'input[name=\"checkin_date\"]');" +
                "var event = new Event('change'," +
                "{bubbles:true});" +
                "el.dispatchEvent(event);");

            System.out.println("Check-in date set: " + date);

        } catch (Exception e) {
            System.out.println("Check-in date error: "
                + e.getMessage());
        }
    }

    // ── Select Check-out Date ─────────────────────────────
    // Same approach as check-in

    public void selectCheckOutDate(String date) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "document.querySelector(" +
                "'input[name=\"checkout_date\"]')" +
                ".value = arguments[0];", date);

            ((JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector(" +
                "'input[name=\"checkout_date\"]');" +
                "var event = new Event('change'," +
                "{bubbles:true});" +
                "el.dispatchEvent(event);");

            System.out.println("Check-out date set: " + date);

        } catch (Exception e) {
            System.out.println("Check-out date error: "
                + e.getMessage());
        }
    }
    
    public void enterNationality(String nation) {
    	wait.waitForVisible(nationalityDropdownBtn);
    	click(nationalityDropdownBtn);
    	
    	driver.findElement(nationalitySearchInput).clear();
    	driver.findElement(nationalitySearchInput).sendKeys(nation);
    	
    	By nationOption = By.xpath(
    		    "//div[contains(@class,'input-dropdown-item')]//span[text()='" 
    		    + nation + "']"
    		);
    	
    	wait.waitForVisible(nationOption);
    	click(nationOption);
    }

    // ── Click Search Button ───────────────────────────────
    // Button has :disabled="isSearching" Alpine.js binding
    // Use JS click to bypass this

    public void clickSearchButton() {
        try {
            WebElement btn = wait.waitForClickable(
                searchButton);
            ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", btn);
            System.out.println("Search button clicked.");

            // Wait for page to load results
            waitForPageLoader();

        } catch (Exception e) {
            // Fallback — find any search button
            try {
                WebElement btn = driver.findElement(
                    By.xpath(
                        "//button[@aria-label='Search Hotels']"));
                ((JavascriptExecutor) driver)
                    .executeScript(
                        "arguments[0].click();", btn);
                System.out.println(
                    "Search button clicked via fallback.");
            } catch (Exception e2) {
                System.out.println("Search button error: "
                    + e2.getMessage());
            }
        }
    }

    // ── Complete Search Flow ──────────────────────────────
    // Convenience method — does all search steps in one call

    public void searchForHotel(String destination) {
        clickStaysTab();
        enterDestination(destination);
        selectCheckInDate("10-06-2026");
        selectCheckOutDate("15-06-2026");
        clickSearchButton();
        System.out.println("Hotel search completed for: "
            + destination);
    }

    // ── Verify Results Displayed ──────────────────────────
    // After search, URL changes to:
    // /stays/dubai/10-06-2026/15-06-2026/IN/2/2-0
    // AND hotel cards appear on page

    public boolean isResultsDisplayed() {
        try {
            // Wait for URL to contain stays
            WebDriverWait urlWait = new WebDriverWait(
                driver, Duration.ofSeconds(20));
            urlWait.until(ExpectedConditions
                .urlContains("stays"));

            System.out.println("Results URL: "
                + driver.getCurrentUrl());
            return true;

        } catch (Exception e) {
            System.out.println("Results not displayed: "
                + e.getMessage());
            return false;
        }
    }

    // ── Get Hotel Count ───────────────────────────────────
    // Reads "8 stays" text from:
    // <span x-text="filteredHotels.length + ' stay'...">8 stays</span>

    public int getHotelCount() {
        try {
            // Wait for loading to finish
            WebDriverWait countWait = new WebDriverWait(
                driver, Duration.ofSeconds(20));

            // Wait for the count span to have text
            WebElement countEl = countWait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.xpath(
                        "//span[contains(text(),'stay')" +
                        " or contains(text(),'stays')]" +
                        "[not(contains(@x-show,'loading'))]")));

            String countText = countEl.getText().trim();
            System.out.println("Hotel count text: "
                + countText);

            // Extract number from "8 stays"
            String numberOnly = countText
                .replaceAll("[^0-9]", "").trim();

            if (!numberOnly.isEmpty()) {
                int count = Integer.parseInt(numberOnly);
                System.out.println("Hotels found: " + count);
                return count;
            }

        } catch (Exception e) {
            System.out.println("Count error: "
                + e.getMessage());
        }

        // Fallback — count hotel card elements
        List<WebElement> cards = driver.findElements(
            By.cssSelector(".hotel-card-animate"));
        System.out.println("Hotel cards found: "
            + cards.size());
        return cards.size();
    }

    // ── Get All Prices ────────────────────────────────────
    // Reads "USD 1545.00" text from price elements
    // Used for dynamic price validation (Q4)

//    public List<Double> getAllPrices() {
//        List<Double> prices = new ArrayList<>();
//        try {
//            List<WebElement> priceElements =
//                driver.findElements(hotelPrices);
//
//            for (WebElement el : priceElements) {
//                String raw = el.getText()
//                    .replaceAll("[^0-9.]", "").trim();
//                if (!raw.isEmpty()) {
//                    try {
//                        prices.add(Double.parseDouble(raw));
//                    } catch (NumberFormatException e) {
//                        // skip non-numeric
//                    }
//                }
//            }
//            System.out.println("Prices extracted: "
//                + prices.size());
//        } catch (Exception e) {
//            System.out.println("Price extraction error: "
//                + e.getMessage());
//        }
//        return prices;
//    }

    // ── Get Hotel Name Counts ─────────────────────────────
    // Used for duplicate detection (Q4)

//    public Map<String, Integer> getHotelNameCounts() {
//        Map<String, Integer> nameCount = new HashMap<>();
//        try {
//            List<WebElement> names =
//                driver.findElements(hotelNames);
//            for (WebElement el : names) {
//                String name = el.getText().trim();
//                if (!name.isEmpty()) {
//                    nameCount.put(name,
//                        nameCount.getOrDefault(name, 0) + 1);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Name count error: "
//                + e.getMessage());
//        }
//        return nameCount;
//    }
//
//    // ── Select First Hotel ────────────────────────────────
//    // Clicks "More Details" button on first hotel card
//    // URL pattern: /stay/{name}/{id}/hotels/...
//
//    public void selectFirstHotel() {
//        try {
//            WebElement moreDetails = wait.waitForClickable(
//                By.xpath(
//                    "(//a[contains(@href,'/stay/')]" +
//                    "[contains(.,'More Details')])[1]"));
//            moreDetails.click();
//            waitForPageLoader();
//            System.out.println("First hotel selected.");
//        } catch (Exception e) {
//            System.out.println("Hotel select error: "
//                + e.getMessage());
//        }
//    }
//
//    // ── Book Now ──────────────────────────────────────────
//
//    public void clickBookNow() {
//        try {
//            WebElement bookBtn = wait.waitForClickable(
//                By.xpath(
//                    "//a[contains(text(),'Book')" +
//                    " or contains(@class,'book')" +
//                    " or contains(text(),'Reserve')]" +
//                    " | //button[contains(.,'Book')]"));
//            bookBtn.click();
//            System.out.println("Book now clicked.");
//        } catch (Exception e) {
//            System.out.println("Book now error: "
//                + e.getMessage());
//        }
//    }
//
//    // ── Fill Traveller Details ────────────────────────────
//
//    public void fillTravellerDetails(String firstName,
//                                      String lastName,
//                                      String phone) {
//        try {
//            WebElement fn = driver.findElement(
//                By.xpath("//input[@name='firstname'" +
//                    " or @id='first_name']"));
//            fn.clear();
//            fn.sendKeys(firstName);
//
//            WebElement ln = driver.findElement(
//                By.xpath("//input[@name='lastname'" +
//                    " or @id='last_name']"));
//            ln.clear();
//            ln.sendKeys(lastName);
//
//            System.out.println("Traveller details filled.");
//        } catch (Exception e) {
//            System.out.println("Traveller details error: "
//                + e.getMessage());
//        }
//    }
//
//    // ── Confirm Booking ───────────────────────────────────
//
//    public void confirmBooking() {
//        try {
//            WebElement confirmBtn = driver.findElement(
//                By.xpath(
//                    "//button[contains(text(),'Confirm')" +
//                    " or contains(text(),'Pay')" +
//                    " or @type='submit']"));
//            ((JavascriptExecutor) driver)
//                .executeScript(
//                    "arguments[0].click();", confirmBtn);
//            System.out.println("Booking confirmed.");
//        } catch (Exception e) {
//            System.out.println("Confirm error: "
//                + e.getMessage());
//        }
//    }
//
//    // ── Is Booking Confirmed ──────────────────────────────
//
//    public boolean isBookingConfirmed() {
//        try {
//            WebDriverWait w = new WebDriverWait(
//                driver, Duration.ofSeconds(10));
//            WebElement msg = w.until(
//                ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath(
//                        "//*[contains(text(),'Confirmed')" +
//                        " or contains(text(),'Booking')" +
//                        " or contains(text(),'Success')" +
//                        " or contains(text(),'Thank')]")));
//            return msg.isDisplayed();
//        } catch (Exception e) {
//            System.out.println("Booking confirm check: "
//                + e.getMessage());
//            return false;
//        }
//    }
}