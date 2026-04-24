package com.bankportal.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardPage extends BasePage {

    private final By dashboardTitle = dataTest("dashboard-title");
    private final By welcomeMessage = dataTest("welcome-message");
    private final By segmentValue = dataTest("segment-value");
    private final By offersCount = dataTest("offers-count");
    private final By offersSection = dataTest("offers-section");
    private final By offerItems = dataTest("offer-item");
    private final By offerTitles = dataTest("offer-title");
    private final By logoutLink = dataTest("logout-link");

    public DashboardPage() {
        super();
    }

    // ===== Navigation =====
    public void open() {
        navigateTo("/dashboard");
        log.info("Opened Dashboard page");
    }

    public LoginPage clickLogout() {
        log.info("Clicking logout");
        click(logoutLink);
        return new LoginPage();
    }

    // ===== Assertions =====
    public String getDashboardTitle() {
        return getText(dashboardTitle);
    }

    public String getWelcomeMessage() {
        return getText(welcomeMessage);
    }

    public String getCustomerSegment() {
        return getText(segmentValue);
    }

    public String getOffersCount() {
        return getText(offersCount);
    }

    public boolean isOffersSectionDisplayed() {
        return isDisplayed(offersSection);
    }

    public int getOfferItemCount() {
        List<WebElement> items = driver.findElements(offerItems);
        log.info("Found {} offers on dashboard", items.size());
        return items.size();
    }

    public List<String> getAllOfferTitles() {
        return driver.findElements(offerTitles)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public boolean isDashboardTitleDisplayed() {
        return isDisplayed(dashboardTitle);
    }
}