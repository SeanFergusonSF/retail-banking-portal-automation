package com.bankportal.api.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CustomerClient extends BaseApiClient {

    public CustomerClient() {
        super();
    }

    public Response getCustomerById(String customerId) {
        log.info("GET /customers/{}", customerId);

        return given()
                .spec(getSpec())
                .when()
                .get("/customers/" + customerId)
                .then()
                .extract()
                .response();
    }
}