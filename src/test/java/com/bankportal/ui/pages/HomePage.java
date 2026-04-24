package com.bankportal.ui.pages;

import org.openqa.selenium.By;

public class HomePage extends BasePage {

    // Locators using data-test attributes
    private final By bankName = dataTest("bank-name");
    private final By heroTitle = dataTest("hero-title");
    private final By heroSubtitle = dataTest("hero-subtitle");
    private final By ctaButton = dataTest("cta-button");
    private final By navProducts = dataTest("nav-products");
    private final By navLogin = dataTest("nav-login");
    private final By productsPreview = dataTest("products-preview");
    private final By previewCardCredit = dataTest("preview-card-credit");
    private final By previewCardLoans = dataTest("preview-card-loans");
    private final By previewCardSavings = dataTest("preview-card-savings");

    public HomePage() {
        super();
    }

    // ===== Navigation =====
    public void open() {
        navigateTo("/");
        log.info("Opened Home page");
    }

    public ProductsPage clickViewOurProducts() {
        log.info("Clicking CTA button - View Our Products");
        click(ctaButton);
        return new ProductsPage();
    }

    public ProductsPage clickNavProducts() {
        log.info("Clicking nav - Products");
        click(navProducts);
        return new ProductsPage();
    }

    public LoginPage clickNavLogin() {
        log.info("Clicking nav - Login");
        click(navLogin);
        return new LoginPage();
    }

    // ===== Assertions =====
    public String getBankName() {
        return getText(bankName);
    }

    public String getHeroTitle() {
        return getText(heroTitle);
    }

    public String getHeroSubtitle() {
        return getText(heroSubtitle);
    }

    public boolean isProductsPreviewDisplayed() {
        return isDisplayed(productsPreview);
    }

    public boolean isCreditCardPreviewDisplayed() {
        return isDisplayed(previewCardCredit);
    }

    public boolean isLoansPreviewDisplayed() {
        return isDisplayed(previewCardLoans);
    }

    public boolean isSavingsPreviewDisplayed() {
        return isDisplayed(previewCardSavings);
    }
}