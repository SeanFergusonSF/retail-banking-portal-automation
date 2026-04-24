package com.bankportal.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    private final By pageTitle = dataTest("products-page-title");
    private final By productList = dataTest("product-list");
    private final By productItems = dataTest("product-item");
    private final By productNames = dataTest("product-name");
    private final By productStatuses = dataTest("product-status");

    public ProductsPage() {
        super();
    }

    // ===== Navigation =====
    public void open() {
        navigateTo("/products");
        log.info("Opened Products page");
    }

    // ===== Assertions =====
    public String getPageTitle() {
        return getText(pageTitle);
    }

    public boolean isProductListDisplayed() {
        return isDisplayed(productList);
    }

    public int getProductCount() {
        List<WebElement> items = driver.findElements(productItems);
        log.info("Found {} products on page", items.size());
        return items.size();
    }

    public List<String> getAllProductNames() {
        return driver.findElements(productNames)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getAllProductStatuses() {
        return driver.findElements(productStatuses)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public long getActiveProductCount() {
        return driver.findElements(productStatuses)
                .stream()
                .map(WebElement::getText)
                .filter(status -> status.equalsIgnoreCase("Active"))
                .count();
    }

    public long getInactiveProductCount() {
        return driver.findElements(productStatuses)
                .stream()
                .map(WebElement::getText)
                .filter(status -> status.equalsIgnoreCase("Inactive"))
                .count();
    }
}