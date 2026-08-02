package com.qa.tests;

import com.qa.framework.base.BaseTest;
import com.qa.framework.pages.DashboardPage;
import com.qa.framework.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * End-to-end smoke test exercising the full stack: DriverManager, ConfigReader,
 * ExtentReportManager, TestListener, and RetryAnalyzer (wired via testng.xml).
 */
public class LoginTest extends BaseTest {

    @Test(description = "Verify a user can log in with valid credentials and land on the dashboard")
    public void testValidLoginLoadsDashboard() {
        String username = config.get("valid.username");
        String password = config.get("valid.password");

        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login(username, password);

        Assert.assertTrue(dashboardPage.isDashboardDisplayed(), "Dashboard header was not displayed after login.");
        Assert.assertEquals(dashboardPage.getHeaderText(), "Dashboard", "Dashboard header text mismatch.");
    }

    @Test(description = "Verify an invalid login attempt shows an error and does not reach the dashboard")
    public void testInvalidLoginShowsError() {
        LoginPage loginPage = new LoginPage();
        loginPage.login("invalidUser", "invalidPass123");

        Assert.assertTrue(loginPage.isInvalidCredentialsAlertDisplayed(), "Invalid credentials alert was not displayed.");
    }
}
