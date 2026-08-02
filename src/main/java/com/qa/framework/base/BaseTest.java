package com.qa.framework.base;

import com.qa.framework.managers.DriverManager;
import com.qa.framework.utils.ConfigReader;
import com.qa.framework.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base class for all test classes. Initializes and tears down a fresh WebDriver
 * per test method via DriverManager, so each thread in a parallel run is fully
 * isolated. Config is read once through the ConfigReader singleton.
 */
public abstract class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected ConfigReader config;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        config = ConfigReader.getInstance();

        String browser = config.get("browser", "chrome");
        boolean headless = config.getBoolean("headless", false);
        int implicitWait = config.getInt("implicit.wait.seconds", 5);
        int pageLoadTimeout = config.getInt("pageload.timeout.seconds", 30);

        logger.info("Initializing driver: browser={}, headless={}", browser, headless);
        WebDriver driver = DriverFactory.createDriver(browser, headless);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().window().maximize();

        DriverManager.setDriver(driver);

        String baseUrl = config.get("base.url");
        driver.get(baseUrl);
        logger.info("Navigated to base URL: {}", baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Tearing down driver for current thread.");
        DriverManager.unload();
    }
}
