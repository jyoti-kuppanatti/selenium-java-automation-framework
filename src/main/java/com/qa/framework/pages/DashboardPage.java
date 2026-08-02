package com.qa.framework.pages;

import org.openqa.selenium.By;

/**
 * Page object for the OrangeHRM dashboard, landed on after a successful login.
 */
public class DashboardPage extends BasePage {

    private final By dashboardHeader = By.xpath("//h6[text()='Dashboard']");

    public DashboardPage() {
        super();
    }

    public boolean isDashboardDisplayed() {
        return waitUtils.waitForVisibility(dashboardHeader).isDisplayed();
    }

    public String getHeaderText() {
        return waitUtils.waitForVisibility(dashboardHeader).getText();
    }
}
