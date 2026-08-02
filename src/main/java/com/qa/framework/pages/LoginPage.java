package com.qa.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for the OrangeHRM login screen.
 */
public class LoginPage extends BasePage {

    private final By usernameInput = By.name("username");
    private final By passwordInput = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By invalidCredsAlert = By.xpath("//p[text()='Invalid credentials']");

    public LoginPage() {
        super();
    }

    public DashboardPage login(String username, String password) {
        WebElement usernameField = waitUtils.waitForVisibility(usernameInput);
        usernameField.clear();
        usernameField.sendKeys(username);

        WebElement passwordField = waitUtils.waitForVisibility(passwordInput);
        passwordField.clear();
        passwordField.sendKeys(password);

        waitUtils.waitForClickable(loginButton).click();

        return new DashboardPage();
    }

    public boolean isInvalidCredentialsAlertDisplayed() {
        return waitUtils.waitForVisibility(invalidCredsAlert).isDisplayed();
    }
}
