package com.bankportal.utils;

import io.restassured.RestAssured;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class WireMockSmokeTest {

    @BeforeClass
    public void startWireMock() {
        WireMockServerManager.start();
        RestAssured.baseURI = WireMockServerManager.getBaseUrl();
    }

    @Test
    public void shouldReturnProductsFromWireMock() {
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("products[0].productId", equalTo("P001"))
                .body("products[0].active", equalTo(true));
    }

    @Test
    public void shouldReturn401WhenNoAuthToken() {
        given()
                .when()
                .get("/offers")
                .then()
                .statusCode(401);
    }

    @AfterClass
    public void stopWireMock() {
        WireMockServerManager.stop();
    }
}