@ui @regression
Feature: Customer Dashboard
  As a logged-in customer
  I want to view my personalised dashboard
  So that I can see my offers and account information

  Scenario: Logged-in customer views personalised offers
    Given a customer is logged into online banking
    When they view their dashboard
    Then the offers section is displayed
    And 2 personalised offers are shown

  Scenario: Dashboard displays correct customer segment
    Given a customer is logged into online banking
    When they view their dashboard
    Then the customer segment displayed is "Premium"