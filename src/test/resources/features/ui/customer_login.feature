@ui @regression
Feature: Customer Login
  As a registered customer
  I want to log into online banking
  So that I can access my account and personalised offers

  Scenario: Valid customer logs in successfully
    Given a customer is on the login page
    When they login with username "standard_user" and password "password123"
    Then they are redirected to the dashboard
    And the welcome message is displayed

  Scenario: Invalid credentials show error message
    Given a customer is on the login page
    When they login with username "wrong_user" and password "wrongpass"
    Then an error message is displayed
    And the error message contains "Invalid username or password"

  Scenario: Locked account shows appropriate message
    Given a customer is on the login page
    When they login with username "locked_user" and password "password123"
    Then an error message is displayed
    And the error message contains "account has been locked"