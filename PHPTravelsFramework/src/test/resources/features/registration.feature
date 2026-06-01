# ============================================================
# Feature  : User Registration
# Site     : phptravels.net/signup
# Q2 from assignment
# ============================================================

@Registration
Feature: PHPTravels User Registration

  Background:
    Given user launches browser

  @Smoke
  Scenario: Successful registration with valid details
    Given user navigates to registration page
    When user fills registration form with details
      | firstName | lastName | phone      | country       |
      | Test      | User     | 9876543210 | United States |
    Then registration should be successful

  @Regression
  Scenario Outline: Registration with different countries
    Given user navigates to registration page
    When user registers with name <firstName> phone <phone> country <country>
    Then registration should be successful

    Examples:
      | firstName | phone      | country        |
      | John      | 9876543210 | United Kingdom |
      | Alice     | 8765432109 | Canada         |