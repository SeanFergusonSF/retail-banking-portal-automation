package com.bankportal.ui.pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By loginTitle = dataTest("login-title");
    private final By loginForm = dataTest("login-form");
    private final By usernameInput = dataTest("username-input");
    private final By passwordInput = dataTest("password-input");
    private final By loginButton = dataTest("login-button");
    private final By errorMessage = dataTest("error-message");

    public LoginPage() {
        super();
    }

    // ===== Navigation =====
    public void open() {
        navigateTo("/login");
        log.info("Opened Login page");
    }

    // ===== Actions =====
    public DashboardPage loginAs(String username, String password) {
        log.info("Logging in as: {}", username);
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
        return new DashboardPage();
    }

    public LoginPage loginWithInvalidCredentials(String username, String password) {
        log.info("Attempting login with invalid credentials: {}", username);
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
        return this;
    }

    // ===== Assertions =====
    public String getLoginTitle() {
        return getText(loginTitle);
    }

    public boolean isLoginFormDisplayed() {
        return isDisplayed(loginForm);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}