package com.bankportal.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtil.class);
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtil() {}

    public static String capture(WebDriver driver, String testName) {
        if (driver == null) {
            log.warn("Driver is null - cannot capture screenshot for: {}", testName);
            return null;
        }

        try {
            // Create screenshots directory if it doesn't exist
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);

            // Generate timestamped filename
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String fileName = testName + "_" + timestamp + ".png";
            Path filePath = screenshotDir.resolve(fileName);

            // Take and save screenshot
            File screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), filePath);

            log.info("Screenshot saved: {}", filePath);
            return filePath.toString();

        } catch (IOException e) {
            log.error("Failed to save screenshot for test: {}", testName, e);
            return null;
        }
    }
}