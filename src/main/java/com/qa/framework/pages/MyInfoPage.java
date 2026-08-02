package com.qa.framework.pages;

import org.openqa.selenium.By;

/**
 * Page object for the PIM > My Info (own employee record) screen.
 */
public class MyInfoPage extends BasePage {

    private final By personalDetailsHeader = By.xpath("//h6[text()='Personal Details']");

    public MyInfoPage() {
        super();
    }

    public boolean isPersonalDetailsDisplayed() {
        return waitUtils.waitForVisibility(personalDetailsHeader).isDisplayed();
    }
}
