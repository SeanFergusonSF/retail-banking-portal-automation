package com.bankportal.api.steps;

import com.bankportal.api.clients.AuthClient;
import com.bankportal.api.clients.OffersClient;
import com.bankportal.api.clients.ProductClient;
import com.bankportal.utils.WireMockServerManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class ApiSteps {

    private static final Logger log = LoggerFactory.getLogger(ApiSteps.class);

    private ProductClient productClient;
    private AuthClient authClient;
    private OffersClient offersClient;
    private Response response;
    private String authToken;

    @Before("@api")
    public void setUp() {
        WireMockServerManager.start();
        productClient = new ProductClient();
        authClient = new AuthClient();
        offersClient = new OffersClient();
    }

    @After("@api")
    public void tearDown() {
        WireMockServerManager.stop();
    }

    // ===== Product API Steps =====

    @Given("the product service is available")
    public void productServiceIsAvailable() {
        log("Product service available via WireMock");
    }

    @When("a GET request is made to the products endpoint")
    public void getRequestToProductsEndpoint() {
        response = productClient.getAllProducts();
    }

    @When("a GET request is made for product {string}")
    public void getRequestForProductById(String productId) {
        response = productClient.getProductById(productId);
    }

    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) {
        Assert.assertEquals(response.getStatusCode(), expectedStatus,
                "Response status code mismatch");
    }

    @Then("the response contains {int} products")
    public void responseContainsProducts(int expectedCount) {
        int actualCount = response.jsonPath().getList("products").size();
        Assert.assertEquals(actualCount, expectedCount,
                "Product count in response mismatch");
    }

    @Then("the response contains {int} active products")
    public void responseContainsActiveProducts(int expectedCount) {
        long activeCount = response.jsonPath()
                .getList("products.active")
                .stream()
                .filter(Boolean.TRUE::equals)
                .count();
        Assert.assertEquals(activeCount, expectedCount,
                "Active product count mismatch");
    }

    @Then("the response contains {int} inactive product")
    public void responseContainsInactiveProducts(int expectedCount) {
        long inactiveCount = response.jsonPath()
                .getList("products.active")
                .stream()
                .filter(Boolean.FALSE::equals)
                .count();
        Assert.assertEquals(inactiveCount, expectedCount,
                "Inactive product count mismatch");
    }

    @Then("the product name is {string}")
    public void productNameIs(String expectedName) {
        String actualName = response.jsonPath().getString("name");
        Assert.assertEquals(actualName, expectedName,
                "Product name mismatch");
    }

    // ===== Auth API Steps =====

    @Given("the auth service is available")
    public void authServiceIsAvailable() {
        log("Auth service available via WireMock");
    }

    @When("a login request is made with valid credentials")
    public void loginWithValidCredentials() {
        response = authClient.login("standard_user", "password123");
    }

    @When("a login request is made with username {string}")
    public void loginWithUsername(String username) {
        response = authClient.login(username, "wrong password");
    }

    @Then("a token is returned in the response")
    public void tokenReturnedInResponse() {
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @Then("the token roles include {string}")
    public void tokenRolesInclude(String expectedRole) {
        Assert.assertTrue(
                response.jsonPath().getList("roles").contains(expectedRole),
                "Roles should include: " + expectedRole
        );
    }

    @Then("the error message is {string}")
    public void errorMessageIs(String expectedMessage) {
        String actualMessage = response.jsonPath().getString("error");
        Assert.assertEquals(actualMessage, expectedMessage,
                "Error message mismatch");
    }

    // ===== Offers API Steps =====

    @Given("a valid authentication token exists")
    public void validAuthTokenExists() {
        authToken = authClient.getValidToken();
        Assert.assertNotNull(authToken, "Auth token should not be null");
    }

    @Given("no authentication token is provided")
    public void noAuthTokenProvided() {
        authToken = null;
        log("Proceeding without authentication token");
    }

    @When("a GET request is made to the offers endpoint with the token")
    public void getOffersWithToken() {
        response = offersClient.getOffersWithToken(authToken);
    }

    @When("a GET request is made to the offers endpoint without a token")
    public void getOffersWithoutToken() {
        response = offersClient.getOffersWithoutToken();
    }

    @Then("{int} offers are returned")
    public void offersAreReturned(int expectedCount) {
        int actualCount = response.jsonPath().getList("offers").size();
        Assert.assertEquals(actualCount, expectedCount,
                "Offer count mismatch");
    }

    @Then("all offers have eligible set to true")
    public void allOffersEligible() {
        response.jsonPath()
                .getList("offers.eligible")
                .forEach(eligible ->
                        Assert.assertEquals(eligible, Boolean.TRUE, "All offers should be eligible"));
    }

    @Then("the response time is under {int} milliseconds")
    public void responseTimeIsUnder(int milliseconds) {
        long responseTime = response.getTime();
        log.info("Response time: {}ms (threshold: {}ms)", responseTime, milliseconds);
        Assert.assertTrue(
                responseTime < milliseconds,
                String.format("Response time %dms exceeded threshold of %dms",
                        responseTime, milliseconds)
        );
    }

    // ===== Utility =====
    private void log(String message) {
        System.out.println("[API Step] " + message);
    }
}