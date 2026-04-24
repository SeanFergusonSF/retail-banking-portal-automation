package com.bankportal.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Starting: {}", context.getName());
        log.info("========================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Finished: {}", context.getName());
        log.info("Passed: {}  Failed: {}  Skipped: {}",
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
        log.info("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("---> Starting test: {}", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✓ PASSED: {}", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✗ FAILED: {}", result.getName());
        log.error("Failure reason: {}", result.getThrowable().getMessage());

        // Capture screenshot if WebDriver is active
        if (DriverManager.isDriverInitialised()) {
            String screenshotPath = ScreenshotUtil.capture(
                    DriverManager.getDriver(),
                    result.getName()
            );
            if (screenshotPath != null) {
                log.info("Screenshot captured: {}", screenshotPath);
            }
        }

        // Always quit the driver after a failure to clean up
        DriverManager.quitDriver();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⚠ SKIPPED: {}", result.getName());
    }
}