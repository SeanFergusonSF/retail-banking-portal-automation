package com.bankportal.utils;

import com.bankportal.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class DriverManager {

    private static final Logger log = LoggerFactory.getLogger(DriverManager.class);

    // ThreadLocal ensures each thread has its own WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private static final ConfigManager config = ConfigManager.getInstance();

    // Private constructor - utility class, not to be instantiated
    private DriverManager() {}

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initialiseDriver();
        }
        return driverThreadLocal.get();
    }

    private static void initialiseDriver() {
        String browser = config.getBrowser();
        log.info("Initialising {} driver (headless: {})", browser, config.isHeadless());

        // WebDriverManager auto-downloads the correct ChromeDriver version
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = createChromeOptions();  // delegated
        WebDriver driver = new ChromeDriver(options);

        // Configure timeouts from config.properties
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()))
                .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));

        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("Driver initialised successfully on thread: {}",
                Thread.currentThread().getName());
    }

    private static ChromeOptions createChromeOptions() {

        ChromeOptions options = new ChromeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }

        // Standard options for stability
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");

        return options;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            log.info("Quitting driver on thread: {}", Thread.currentThread().getName());
            driver.quit();
            // Remove from ThreadLocal to prevent memory leaks
            driverThreadLocal.remove();
        }
    }

    public static boolean isDriverInitialised() {
        return driverThreadLocal.get() != null;
    }
}