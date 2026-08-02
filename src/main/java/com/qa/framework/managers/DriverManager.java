package com.qa.framework.managers;

import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver holder backed by ThreadLocal, so each TestNG worker
 * thread in a parallel run gets its own isolated driver instance.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized for this thread. " +
                    "Call DriverManager.setDriver() before use.");
        }
        return driver;
    }

    public static void unload() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
        }
        driverThreadLocal.remove();
    }
}
