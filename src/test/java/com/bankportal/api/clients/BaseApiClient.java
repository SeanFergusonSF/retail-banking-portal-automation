package com.bankportal.api.clients;

import com.bankportal.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseApiClient {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final RequestSpecification requestSpec;
    protected final ConfigManager config;

    public BaseApiClient() {
        this.config = ConfigManager.getInstance();

        // Build a base request specification all clients inherit
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getWireMockBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .log(LogDetail.URI)
                .build();

        // Disable REST Assured's default URL encoding
        // to prevent issues with special characters in paths
        RestAssured.urlEncodingEnabled = false;
    }

    // Subclasses can override to add auth headers
    protected RequestSpecification getSpec() {
        return requestSpec;
    }

    protected RequestSpecification getAuthenticatedSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}