@accessibility
Feature: Accessibility — WCAG 2.1 compliance across marketing portal pages

  # axe-core automated scanning covers approximately 30-40% of WCAG 2.1 issues
  # Critical and serious violations fail the test
  # Moderate and minor violations are logged but do not block
  # Manual assistive technology testing (VoiceOver, NVDA) required for full coverage

  Background:
    Given the browser is open

  @accessibility @smoke
  Scenario: Homepage meets WCAG 2.1 accessibility standards
    When the customer navigates to the homepage
    Then the page has no critical or serious accessibility violations

  @accessibility @smoke
  Scenario: Products page meets WCAG 2.1 accessibility standards
    When the customer navigates to the products page
    Then the page has no critical or serious accessibility violations

  @accessibility @smoke
  Scenario: Login page meets WCAG 2.1 accessibility standards
    When the customer navigates to the login page
    Then the page has no critical or serious accessibility violations

  @accessibility @smoke
  Scenario: Dashboard page meets WCAG 2.1 accessibility standards
    When the customer navigates to the dashboard page
    Then the page has no critical or serious accessibility violations