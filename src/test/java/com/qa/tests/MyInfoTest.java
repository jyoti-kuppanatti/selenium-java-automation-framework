package com.qa.tests;

import com.qa.framework.base.BaseTest;
import com.qa.framework.pages.DashboardPage;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.pages.MyInfoPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Covers the PIM > My Info screen for the logged-in user.
 */
public class MyInfoTest extends BaseTest {

    @Test(description = "Verify My Info navigation shows the logged-in user's Personal Details section")
    public void testMyInfoShowsPersonalDetails() {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login(config.get("valid.username"), config.get("valid.password"));

        MyInfoPage myInfoPage = dashboardPage.goToMyInfo();

        Assert.assertTrue(myInfoPage.isPersonalDetailsDisplayed(), "Personal Details section was not displayed on My Info page.");
    }
}
