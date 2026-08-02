package com.qa.framework.pages;

import org.openqa.selenium.By;

/**
 * Page object for the OrangeHRM dashboard, landed on after a successful login.
 */
public class DashboardPage extends BasePage {

    private final By dashboardHeader = By.xpath("//h6[text()='Dashboard']");
    private final By pimMenuLink = By.xpath("//a[contains(@href,'viewPimModule')]");
    private final By myInfoMenuLink = By.xpath("//a[contains(@href,'viewMyDetails')]");
    private final By userDropdownTab = By.cssSelector(".oxd-userdropdown-tab");
    private final By logoutLink = By.xpath("//a[text()='Logout']");

    public DashboardPage() {
        super();
    }

    public boolean isDashboardDisplayed() {
        return waitUtils.waitForVisibility(dashboardHeader).isDisplayed();
    }

    public String getHeaderText() {
        return waitUtils.waitForVisibility(dashboardHeader).getText();
    }

    public PimEmployeeListPage goToPim() {
        waitUtils.waitForClickable(pimMenuLink).click();
        return new PimEmployeeListPage();
    }

    public MyInfoPage goToMyInfo() {
        waitUtils.waitForClickable(myInfoMenuLink).click();
        return new MyInfoPage();
    }

    public LoginPage logout() {
        waitUtils.waitForClickable(userDropdownTab).click();
        waitUtils.waitForClickable(logoutLink).click();
        return new LoginPage();
    }
}
