# ============================================================
# Feature  : Login Module
# Site     : phptravels.net/login
# Q1 from assignment
# ============================================================

@Login
Feature: PHPTravels Login Module

  # Scenario Outline + Examples = Data Driven Testing
  # Each row in Examples runs as a separate scenario
  # Credentials come from here but are also in Excel —
  # step definitions read from Excel file

  @Smoke @Regression
  Scenario Outline: Validate Login Functionality
    Given user launches browser
    When user enters "<username>" and "<password>"
    And clicks on login button
    Then validate login result "<expectedResult>"

    # These rows match exactly what is in LoginData.xlsx
    # valid credentials → expect success
    # invalid/blank     → expect failure
    Examples:
      | username                 | password  | expectedResult |
      | user@phptravels.com      | demouser  | success        |
      | invalid@gmail.com        | invalid   | failure        |
      |                          | demouser  | failure        |
      | user@phptravels.com      |           | failure        |