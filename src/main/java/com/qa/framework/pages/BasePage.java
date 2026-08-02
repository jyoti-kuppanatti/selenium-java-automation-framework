package com.qa.framework.pages;

import com.qa.framework.managers.DriverManager;
import com.qa.framework.utils.ConfigReader;
import com.qa.framework.utils.WaitUtils;
import org.openqa.selenium.WebDriver;

/**
 * Common base for all page objects. Fetches the current thread's WebDriver
 * via DriverManager rather than holding a driver reference directly, keeping
 * page objects safe to use under parallel execution.
 */
public abstract class BasePage {

    protected final WaitUtils waitUtils;

    protected BasePage() {
        int timeout = ConfigReader.getInstance().getInt("explicit.wait.seconds", 15);
        this.waitUtils = new WaitUtils(getDriver(), timeout);
    }

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }
}
