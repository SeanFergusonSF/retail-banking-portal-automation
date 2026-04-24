package com.bankportal.ui.steps;

import com.bankportal.ui.pages.DashboardPage;
import com.bankportal.ui.pages.HomePage;
import com.bankportal.ui.pages.LoginPage;
import com.bankportal.ui.pages.ProductsPage;
import com.bankportal.utils.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class UiSteps {

    private HomePage homePage;
    private ProductsPage productsPage;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    @Before("@ui")
    public void setUp() {
        // Driver is initialised lazily on first getDriver() call
        homePage = new HomePage();
        loginPage = new LoginPage();
        productsPage = new ProductsPage();
        dashboardPage = new DashboardPage();
    }

    @After("@ui")
    public void tearDown() {
        DriverManager.quitDriver();
    }

    // ===== Home Page Steps =====

    @Given("a customer accesses the marketing homepage")
    public void customerAccessesHomepage() {
        homePage.open();
    }

    @Then("the bank name is displayed")
    public void bankNameIsDisplayed() {
        Assert.assertEquals(homePage.getBankName(), "NorthBank",
                "Bank name should be NorthBank");
    }

    @Then("the hero title is visible")
    public void heroTitleIsVisible() {
        Assert.assertTrue(
                homePage.getHeroTitle().contains("NorthBank"),
                "Hero title should contain NorthBank"
        );
    }

    @Then("the products preview section is displayed")
    public void productsPreviewIsDisplayed() {
        Assert.assertTrue(homePage.isProductsPreviewDisplayed(),
                "Products preview section should be visible");
    }

    @When("they click the View Our Products button")
    public void clickViewOurProducts() {
        productsPage = homePage.clickViewOurProducts();
    }

    @When("they click Login in the navigation")
    public void clickNavLogin() {
        loginPage = homePage.clickNavLogin();
    }

    @Then("they are taken to the login page")
    public void takenToLoginPage() {
        Assert.assertTrue(loginPage.isLoginFormDisplayed(),
                "Login form should be displayed");
    }

    // ===== Products Page Steps =====

    @Given("a customer navigates to the products page")
    public void customerNavigatesToProductsPage() {
        productsPage.open();
    }

    @Then("they are taken to the products page")
    public void takenToProductsPage() {
        Assert.assertTrue(productsPage.isProductListDisplayed(),
                "Product list should be displayed");
    }

    @Then("the product list is displayed")
    public void productListIsDisplayed() {
        Assert.assertTrue(productsPage.isProductListDisplayed(),
                "Product list should be visible");
    }

    @Then("{int} products are displayed in total")
    public void productsDisplayedInTotal(int expectedCount) {
        Assert.assertEquals(productsPage.getProductCount(), expectedCount,
                "Total product count mismatch");
    }

    @Then("{int} products have a status of Active")
    public void productsHaveStatusActive(int expectedCount) {
        Assert.assertEquals(productsPage.getActiveProductCount(), (long) expectedCount,
                "Active product count mismatch");
    }

    @Then("{int} product has a status of Inactive")
    public void productHasStatusInactive(int expectedCount) {
        Assert.assertEquals(productsPage.getInactiveProductCount(), (long) expectedCount,
                "Inactive product count mismatch");
    }

    @Then("the product list contains {string}")
    public void productListContains(String productName) {
        Assert.assertTrue(
                productsPage.getAllProductNames().contains(productName),
                "Product list should contain: " + productName
        );
    }

    @Then("that product is marked as Inactive")
    public void productIsMarkedInactive() {
        Assert.assertTrue(
                productsPage.getAllProductStatuses().contains("Inactive"),
                "At least one product should be marked Inactive"
        );
    }

    // ===== Login Page Steps =====

    @Given("a customer is on the login page")
    public void customerIsOnLoginPage() {
        loginPage.open();
    }

    @When("they login with username {string} and password {string}")
    public void loginWithCredentials(String username, String password) {
        if (username.equals("standard_user")) {
            dashboardPage = loginPage.loginAs(username, password);
        } else {
            loginPage.loginWithInvalidCredentials(username, password);
        }
    }

    @Then("they are redirected to the dashboard")
    public void redirectedToDashboard() {
        Assert.assertTrue(dashboardPage.isDashboardTitleDisplayed(),
                "Dashboard title should be displayed after login");
    }

    @Then("the welcome message is displayed")
    public void welcomeMessageIsDisplayed() {
        Assert.assertFalse(
                dashboardPage.getWelcomeMessage().isEmpty(),
                "Welcome message should not be empty"
        );
    }

    @Then("an error message is displayed")
    public void errorMessageIsDisplayed() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error message should be displayed");
    }

    @Then("the error message contains {string}")
    public void errorMessageContains(String expectedText) {
        Assert.assertTrue(
                loginPage.getErrorMessage().contains(expectedText),
                "Error message should contain: " + expectedText
        );
    }

    // ===== Dashboard Steps =====

    @Given("a customer is logged into online banking")
    public void customerIsLoggedIn() {
        loginPage.open();
        dashboardPage = loginPage.loginAs("standard_user", "password123");
    }

    @When("they view their dashboard")
    public void theyViewDashboard() {
        Assert.assertTrue(dashboardPage.isDashboardTitleDisplayed(),
                "Should be on dashboard");
    }

    @Then("the offers section is displayed")
    public void offersSectionIsDisplayed() {
        Assert.assertTrue(dashboardPage.isOffersSectionDisplayed(),
                "Offers section should be displayed");
    }

    @Then("{int} personalised offers are shown")
    public void personalisedOffersShown(int expectedCount) {
        Assert.assertEquals(dashboardPage.getOfferItemCount(), expectedCount,
                "Offer count mismatch");
    }

    @Then("the customer segment displayed is {string}")
    public void customerSegmentIs(String expectedSegment) {
        Assert.assertEquals(dashboardPage.getCustomerSegment(), expectedSegment,
                "Customer segment mismatch");
    }
}