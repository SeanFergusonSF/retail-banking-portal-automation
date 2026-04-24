package com.bankportal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties properties = new Properties();

    // Private constructor - Singleton pattern
    private ConfigManager() {
        loadProperties();
    }

    // JVM guarantees this inner class is loaded lazily and exactly once
    private static class Holder {
        private static final ConfigManager INSTANCE = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    private void loadProperties() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            properties.load(input);
            log.info("Configuration loaded successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // ===== Generic getter =====
    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property not found in config.properties: " + key);
        }
        return value.trim();
    }

    // ===== Typed getters =====
    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    // ===== Convenience methods =====
    public String getBaseUrl() {
        String env = get("env");
        return get(env + ".base.url");
    }

    public String getWireMockBaseUrl() {
        return "http://" + get("wiremock.host") + ":" + get("wiremock.port");
    }

    public String getBrowser() {
        return get("browser");
    }

    public boolean isHeadless() {
        return getBoolean("headless");
    }

    public int getImplicitWait() {
        return getInt("implicit.wait");
    }

    public int getExplicitWait() {
        return getInt("explicit.wait");
    }

    public int getPageLoadTimeout() {
        return getInt("page.load.timeout");
    }
}