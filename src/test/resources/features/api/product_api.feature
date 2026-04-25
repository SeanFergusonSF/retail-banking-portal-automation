@api @smoke
Feature: Product Catalogue API
  As a consumer of the Product Service
  I want to retrieve banking products via the API
  So that I can validate the product catalogue is correct

  Scenario: Retrieve all products returns 200
    Given the product service is available
    When a GET request is made to the products endpoint
    Then the response status is 200
    And the response contains 3 products

  Scenario: Only active products are available for customer display
    Given the product service is available
    When a GET request is made to the products endpoint
    Then the response contains 2 active products
    And the response contains 1 inactive product

  Scenario: Retrieve product by ID returns correct product
    Given the product service is available
    When a GET request is made for product "P001"
    Then the response status is 200
    And the product name is "Classic Credit Card"

  @performance
  Scenario: Product API responds within acceptable time threshold
    Given the product service is available
    When a GET request is made to the products endpoint
    Then the response status is 200
    And the response time is under 500 milliseconds