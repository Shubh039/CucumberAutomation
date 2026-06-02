Feature: PHPTravels Automation  
	@Regression
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
		
	@Smoke @Regression
	Scenario: Validate User Registration
	    Given user opens registration page
	    When user enters all mandatory registration details
	    And user submits the registration form
	    Then registration should be successful
	    
  	@Smoke @Regression
  	Scenario: Validate hotel search
    	Given user launches browser
	    And user is on PHPTravels home page
	    When user clicks on Stays tab
	    And user enters hotel destination "Dubai"
	    And user selects checkin date "10-06-2026"
	    And user selects checkout date "15-06-2026"
	   	And user selects nationality "Australia"
	    And user clicks hotel search button
	    Then hotel search results should be displayed
	    And available hotels count should be shown
	    
	 @Smoke @Regression
     Scenario: Validate Complete Booking Workflow
     	Given user launches browser
     	When user enters "user@phptravels.com" and "demouser"
     	And clicks on login button
     	Then validate login result "success"
    	When user is on PHPTravels home page
    	And user clicks on Stays tab
    	And user enters hotel destination "Dubai"
    	And user selects checkin date "10-06-2026"
    	And user selects checkout date "15-06-2026"
	    And user selects "3" guests and "2" rooms
	    And user selects nationality "India"
	    And user clicks hotel search button
	    Then hotel search results should be displayed
	    When user selects the first available hotel
	    And user clicks book now button
	    And user enters traveller first name "Test" last name "User"
	    And user confirms the booking
	    Then booking confirmation should be displayed
	    When user logs out from account
	    Then logout should be successful
		
 
	
	