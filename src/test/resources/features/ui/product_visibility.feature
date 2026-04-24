@ui @regression
Feature: Product Visibility
  As a potential customer
  I want to view available banking products
  So that I can choose the right product for my needs

  Scenario: All products are displayed on the products page
    Given a customer navigates to the products page
    Then 3 products are displayed in total
    And 2 products have a status of Active
    And 1 product has a status of Inactive

  Scenario: Active credit card product is visible
    Given a customer navigates to the products page
    Then the product list contains "Classic Credit Card"

  Scenario: Inactive product is clearly labelled
    Given a customer navigates to the products page
    Then the product list contains "Legacy Savings Account"
    And that product is marked as Inactive