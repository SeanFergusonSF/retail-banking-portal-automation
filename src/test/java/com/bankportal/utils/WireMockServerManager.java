package com.bankportal.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WireMockServerManager {

    private static final Logger log = LoggerFactory.getLogger(WireMockServerManager.class);
    private static WireMockServer wireMockServer;
    private static final int PORT = 8089;

    public static void start() {
        if (wireMockServer == null || !wireMockServer.isRunning()) {
            wireMockServer = new WireMockServer(
                    WireMockConfiguration.wireMockConfig()
                            .port(PORT)
                            .usingFilesUnderClasspath("wiremock")
            );
            wireMockServer.start();
            log.info("WireMock server started on port {}", PORT);
        }
    }

    public static void stop() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            log.info("WireMock server stopped");
        }
    }

    public static String getBaseUrl() {
        return "http://localhost:" + PORT;
    }
}