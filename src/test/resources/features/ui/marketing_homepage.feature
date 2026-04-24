@ui @smoke
Feature: Marketing Homepage
  As a potential customer
  I want to view the NorthBank marketing homepage
  So that I can learn about available products and services

  Scenario: Homepage displays bank branding and hero content
    Given a customer accesses the marketing homepage
    Then the bank name is displayed
    And the hero title is visible
    And the products preview section is displayed

  Scenario: Customer navigates to products from homepage CTA
    Given a customer accesses the marketing homepage
    When they click the View Our Products button
    Then they are taken to the products page
    And the product list is displayed

  Scenario: Customer navigates to login from homepage navigation
    Given a customer accesses the marketing homepage
    When they click Login in the navigation
    Then they are taken to the login page