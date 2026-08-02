package com.qa.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page object for the PIM > Employee List screen.
 */
public class PimEmployeeListPage extends BasePage {

    private final By pageHeader = By.xpath("//h5[text()='Employee Information']");
    private final By employeeIdInput = By.xpath("//label[text()='Employee Id']/ancestor::div[contains(@class,'oxd-input-group')]//input");
    private final By searchButton = By.cssSelector("button[type='submit']");
    private final By noRecordsFoundText = By.xpath("//span[text()='No Records Found']");
    private final By resultRows = By.cssSelector(".oxd-table-card");

    public PimEmployeeListPage() {
        super();
    }

    public boolean isEmployeeListPageDisplayed() {
        return waitUtils.waitForVisibility(pageHeader).isDisplayed();
    }

    public PimEmployeeListPage waitForResultsToLoad() {
        waitUtils.until(d -> !d.findElements(resultRows).isEmpty() || !d.findElements(noRecordsFoundText).isEmpty());
        return this;
    }

    public PimEmployeeListPage searchByEmployeeId(String employeeId) {
        WebElement idField = waitUtils.waitForVisibility(employeeIdInput);
        idField.clear();
        idField.sendKeys(employeeId);
        waitUtils.waitForClickable(searchButton).click();
        return waitForResultsToLoad();
    }

    public int getResultRowCount() {
        return getDriver().findElements(resultRows).size();
    }

    public boolean isNoRecordsFoundDisplayed() {
        return !getDriver().findElements(noRecordsFoundText).isEmpty();
    }
}
