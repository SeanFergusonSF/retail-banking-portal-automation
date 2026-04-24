@api @smoke
Feature: Authentication API
  As a consumer of the Auth Service
  I want to authenticate via the API
  So that I can obtain a valid token for subsequent requests

  Scenario: Valid credentials return authentication token
    Given the auth service is available
    When a login request is made with valid credentials
    Then the response status is 200
    And a token is returned in the response
    And the token roles include "CUSTOMER"

  Scenario: Invalid credentials return 401
    Given the auth service is available
    When a login request is made with username "invalid_user"
    Then the response status is 401
    And the error message is "Unauthorised"