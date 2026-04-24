package com.bankportal.api.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ProductClient extends BaseApiClient {

    public ProductClient() {
        super();
    }

    public Response getAllProducts() {
        log.info("GET /products");

        return given()
                .spec(getSpec())
                .when()
                .get("/products")
                .then()
                .extract()
                .response();
    }

    public Response getProductById(String productId) {
        log.info("GET /products/{}", productId);

        return given()
                .spec(getSpec())
                .when()
                .get("/products/" + productId)
                .then()
                .extract()
                .response();
    }
}