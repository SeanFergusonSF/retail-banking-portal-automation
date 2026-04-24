@api @regression
Feature: Offers API
  As a consumer of the Offers Service
  I want to retrieve personalised offers via the API
  So that I can validate offer eligibility logic

  Scenario: Authenticated request returns personalised offers
    Given a valid authentication token exists
    When a GET request is made to the offers endpoint with the token
    Then the response status is 200
    And 2 offers are returned
    And all offers have eligible set to true

  Scenario: Unauthenticated request is rejected
    Given no authentication token is provided
    When a GET request is made to the offers endpoint without a token
    Then the response status is 401
    And the error message is "Unauthorised"