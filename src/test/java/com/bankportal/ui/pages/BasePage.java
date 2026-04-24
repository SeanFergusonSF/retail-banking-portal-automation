package com.bankportal.ui.pages;

import com.bankportal.config.ConfigManager;
import com.bankportal.utils.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final ConfigManager config;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.config = ConfigManager.getInstance();
        this.wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(config.getExplicitWait())
        );
        // Initialises @FindBy annotated fields in subclasses
        PageFactory.initElements(driver, this);
    }

    // ===== Navigation =====
    public void navigateTo(String path) {
        String url = config.getBaseUrl() + path;
        log.info("Navigating to: {}", url);
        driver.get(url);
    }

    // ===== Wait utilities =====
    protected WebElement waitForVisible(By locator) {
        log.debug("Waiting for element to be visible: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        log.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean waitForInvisible(By locator) {
        log.debug("Waiting for element to be invisible: {}", locator);
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ===== Interaction utilities =====
    protected void click(By locator) {
        log.debug("Clicking element: {}", locator);
        waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        log.debug("Typing '{}' into element: {}", text, locator);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    // ===== JavaScript utilities =====
    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void jsClick(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element);
    }

    // ===== Page state =====
    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ===== data-test attribute locator helper =====
    protected By dataTest(String value) {
        return By.cssSelector("[data-test='" + value + "']");
    }
}