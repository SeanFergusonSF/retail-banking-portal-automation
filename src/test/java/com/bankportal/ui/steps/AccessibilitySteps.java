package com.bankportal.ui.steps;

import com.bankportal.config.ConfigManager;
import com.bankportal.utils.DriverManager;
import com.bankportal.utils.AxeHelper;
import com.deque.html.axecore.results.Results;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

/**
 * Step definitions for WCAG 2.1 accessibility scanning.
 *
 * Uses axe-core injected via Selenium to scan each page.
 * Fails on critical and serious violations only.
 * Moderate and minor violations are logged for review.
 */
public class AccessibilitySteps {

    private static final Logger log =
            LoggerFactory.getLogger(AccessibilitySteps.class);

    private final ConfigManager config = ConfigManager.getInstance();
    private WebDriver driver;
    private Results axeResults;

    @Before("@accessibility")
    public void setUp() {
        driver = DriverManager.getDriver();
        log.info("Browser initialised for accessibility scenario");
    }

    @After("@accessibility")
    public void tearDown() {
        DriverManager.quitDriver();
        log.info("Browser closed after accessibility scenario");
    }

    @Given("the browser is open")
    public void theBrowserIsOpen() {
        log.info("Browser ready for accessibility scanning");
    }

    @When("the customer navigates to the homepage")
    public void navigateToHomepage() {
        String url = config.getBaseUrl() + "/";
        log.info("Navigating to homepage: {}", url);
        driver.get(url);
    }

    @When("the customer navigates to the products page")
    public void navigateToProductsPage() {
        String url = config.getBaseUrl() + "/products";
        log.info("Navigating to products page: {}", url);
        driver.get(url);
    }

    @When("the customer navigates to the login page")
    public void navigateToLoginPage() {
        String url = config.getBaseUrl() + "/login";
        log.info("Navigating to login page: {}", url);
        driver.get(url);
    }

    @When("the customer navigates to the dashboard page")
    public void navigateToDashboardPage() {
        // Dashboard requires login first
        // Navigate via login flow to get authenticated state
        String loginUrl = config.getBaseUrl() + "/login";
        log.info("Navigating to login to reach dashboard: {}", loginUrl);
        driver.get(loginUrl);

        // Use standard_user credentials to reach dashboard
        driver.findElement(
                        org.openqa.selenium.By.cssSelector(
                                "[data-test='username-input']"))
                .sendKeys(config.getStandardUsername());

        driver.findElement(
                        org.openqa.selenium.By.cssSelector(
                                "[data-test='password-input']"))
                .sendKeys(config.getStandardPassword());

        driver.findElement(
                        org.openqa.selenium.By.cssSelector(
                                "[data-test='login-button']"))
                .click();

        log.info("Logged in — now on dashboard for accessibility scan");
    }

    @Then("the page has no critical or serious accessibility violations")
    public void pageHasNoCriticalOrSeriousViolations() {
        axeResults = AxeHelper.runScan(driver);

        String violationSummary =
                AxeHelper.formatViolationSummary(axeResults);

        Assert.assertTrue(
                AxeHelper.hasNoCriticalOrSeriousViolations(axeResults),
                "Accessibility violations found on " +
                        driver.getCurrentUrl() + ":\n" + violationSummary
        );

        log.info("Accessibility check passed for: {}",
                driver.getCurrentUrl());
    }
}