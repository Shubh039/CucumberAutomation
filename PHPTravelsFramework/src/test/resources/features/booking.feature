# ============================================================
# Feature  : Complete Booking Workflow
# Site     : phptravels.net
# Q5 from assignment
# ============================================================

@Booking
Feature: PHPTravels Complete Booking Workflow

  @Regression
  Scenario: End to end hotel booking flow
    Given user launches browser
    And user logs in with valid credentials
    And user is on the home page
    When user enters destination "Dubai"
    And user selects check-in date "12/20/2026"
    And user selects check-out date "12/25/2026"
    And user clicks search button
    Then search results should be displayed
    When user selects first available hotel
    And user proceeds to book the hotel
    And user fills traveller details "Test" "User" "9876543210"
    And user confirms the booking
    Then booking confirmation message should appear
    When user logs out
    Then user should be redirected to home page
     
	@Smoke @Regression
  	Scenario Outline: Validate Login Functionality
    	Given user launches browser
    	When user enters "<username>" and "<password>"
    	And clicks on login button
    	Then validate login result "<expectedResult>"	

    Examples:
      | username                 | password  | expectedResult |
      | user@phptravels.com      | demouser  | success        |
      | invalid@gmail.com        | invalid   | failure        |
      |                          | demouser  | failure        |
      | user@phptravels.com      |           | failure        |
      
	@Regression
	Scenario: Validate Login Functionality Using Excel   
		Given user launches browser
		When user performs login using excel data
		Then excel login validation should complete 
	
	@Regression
	Scenario: Validate User Registration
		Given user opens registration page 
		When user enters all mandatory registration details 
		Then registration should be successful 
	
	@Smoke @Regression
	Scenario: Validate hotel search 
		Given user is on PHPTravels home page
		When user searches hotel for destination "Dubai"
		Then hotel search results should be displayed 
		
	@Regression
	Scenario: Validate Dynamic Hotel Prices
		Given user is logged into PHPtravels 
		When user searches hotel for destination "Dubai"
		Then validate hotel prices 
	
	@Smoke @Regression
	Scenario: Validate Complete Booking Workflow
		Given user is logged into PHPtravels
		When user completes hotel booking flow 
		Then booking confirmation message should be displayed 
	
	@Regression 
	Scenario:  Validate Dynamic Booking Table
		Given user is on PHPtravels home page 
		Then validate booking table dynamically 

		