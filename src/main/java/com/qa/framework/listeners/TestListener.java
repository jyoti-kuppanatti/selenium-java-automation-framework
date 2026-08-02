package com.qa.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.qa.framework.managers.DriverManager;
import com.qa.framework.utils.ExtentReportManager;
import com.qa.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Bridges TestNG lifecycle events into ExtentReports. Screenshot capture on
 * failure is triggered here and embedded into the report node for that test.
 * Framework/infra events are also logged via log4j2; per-step detail lives in
 * ExtentReports, not the log file.
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentReportManager.createTest(testName, result.getMethod().getDescription());
        test.assignCategory(result.getTestContext().getCurrentXmlTest().getName());
        logger.info("Test started: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.getTest().log(Status.PASS, "Test passed: " + testName);
        logger.info("Test passed: {}", testName);
        ExtentReportManager.unload();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.FAIL, "Test failed: " + testName);
        test.log(Status.FAIL, result.getThrowable());
        logger.error("Test failed: {}", testName, result.getThrowable());

        try {
            WebDriver driver = DriverManager.getDriver();
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, testName);
            if (screenshotPath != null) {
                test.addScreenCaptureFromPath(screenshotPath, testName + "_failure");
            }
        } catch (Exception e) {
            logger.error("Could not attach screenshot for failed test '{}'", testName, e);
        }
        ExtentReportManager.unload();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentTest test = ExtentReportManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, "Test skipped: " + testName);
        }
        logger.warn("Test skipped: {}", testName);
        ExtentReportManager.unload();
    }

    @Override
    public void onStart(ITestContext context) {
        logger.info("Test suite started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("Test suite finished: {}", context.getName());
        ExtentReportManager.flush();
    }
}
