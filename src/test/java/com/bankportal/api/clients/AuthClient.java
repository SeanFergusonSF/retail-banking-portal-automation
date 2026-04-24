package com.bankportal.api.clients;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient extends BaseApiClient {

    public AuthClient() {
        super();
    }

    public Response login(String username, String password) {
        log.info("POST /auth/login for user: {}", username);

        String requestBody = String.format(
                "{\"username\": \"%s\", \"password\": \"%s\"}",
                username, password
        );

        return given()
                .spec(getSpec())
                .body(requestBody)
                .when()
                .post("/auth/login")
                .then()
                .extract()
                .response();
    }

    public String getValidToken() {
        Response response = login("standard_user", "password123");
        String token = response.jsonPath().getString("token");
        log.info("Retrieved auth token: {}...", token.substring(0, 20));
        return token;
    }
}