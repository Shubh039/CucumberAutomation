# ============================================================
# Feature  : Hotel Search Automation
# Site     : phptravels.net
# Question 3 from assignment
# ============================================================

@HotelSearch
Feature: PHPTravels Hotel Search Automation

  Background:
    Given user launches browser
    And user is on PHPTravels home page

  # ── SMOKE ────────────────────────────────────────────────

  @Smoke
  Scenario: Validate hotel search for Dubai
    Given user clicks on Stays tab
    When user enters hotel destination "Dubai"
    And user selects checkin date "10-06-2026"
    And user selects checkout date "15-06-2026"
    And user clicks hotel search button
    Then hotel search results should be displayed
    And available hotels count should be shown

  # ── REGRESSION ───────────────────────────────────────────

  @Regression
  Scenario Outline: Validate hotel search for multiple destinations
    Given user clicks on Stays tab
    When user enters hotel destination <destination>
    And user selects checkin date "10-06-2026"
    And user selects checkout date "15-06-2026"
    And user clicks hotel search button
    Then hotel search results should be displayed
    And available hotels count should be shown

    Examples:
      | destination |
      | Dubai       |
      | London      |
      | Paris       |