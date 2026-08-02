package com.qa.tests;

import com.qa.framework.base.BaseTest;
import com.qa.framework.pages.DashboardPage;
import com.qa.framework.pages.LoginPage;
import com.qa.framework.pages.PimEmployeeListPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Covers the PIM > Employee List screen: default listing and a no-match search.
 */
public class PimEmployeeListTest extends BaseTest {

    private PimEmployeeListPage loginAndOpenPim() {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login(config.get("valid.username"), config.get("valid.password"));
        return dashboardPage.goToPim();
    }

    @Test(description = "Verify the PIM employee list loads with records by default")
    public void testEmployeeListLoadsRecords() {
        PimEmployeeListPage pimPage = loginAndOpenPim();
        pimPage.waitForResultsToLoad();

        Assert.assertTrue(pimPage.isEmployeeListPageDisplayed(), "Employee Information header was not displayed.");
        Assert.assertTrue(pimPage.getResultRowCount() > 0, "Expected at least one employee record in the default list.");
    }

    @Test(description = "Verify searching for a non-existent employee ID shows 'No Records Found'")
    public void testSearchByUnknownEmployeeIdShowsNoRecords() {
        PimEmployeeListPage pimPage = loginAndOpenPim();
        pimPage.searchByEmployeeId("9999999");

        Assert.assertTrue(pimPage.isNoRecordsFoundDisplayed(), "'No Records Found' message was not displayed for an unknown employee ID.");
        Assert.assertEquals(pimPage.getResultRowCount(), 0, "Expected zero rows for an unknown employee ID search.");
    }
}
