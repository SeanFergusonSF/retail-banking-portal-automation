package com.bankportal.api.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OffersClient extends BaseApiClient {

    public OffersClient() {
        super();
    }

    public Response getOffersWithToken(String token) {
        log.info("GET /offers with Bearer token");

        return given()
                .spec(getAuthenticatedSpec(token))
                .when()
                .get("/offers")
                .then()
                .extract()
                .response();
    }

    public Response getOffersWithoutToken() {
        log.info("GET /offers without token");

        return given()
                .spec(getSpec())
                .when()
                .get("/offers")
                .then()
                .extract()
                .response();
    }
}