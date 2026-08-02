package com.qa.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton wrapper around the shared ExtentReports instance (one HTML report per run).
 * A ThreadLocal<ExtentTest> tracks the "current test" per worker thread so that tests
 * running in parallel each log to their own report node instead of interleaving.
 */
public class ExtentReportManager {

    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static final String REPORT_DIR = "extent-reports";

    private static volatile ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {
    }

    public static ExtentReports getInstance() {
        if (extentReports == null) {
            synchronized (ExtentReportManager.class) {
                if (extentReports == null) {
                    extentReports = createInstance();
                }
            }
        }
        return extentReports;
    }

    private static ExtentReports createInstance() {
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = REPORT_DIR + File.separator + "TestReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Automation Test Report");
        sparkReporter.config().setReportName("OrangeHRM Regression Suite");

        ExtentReports reports = new ExtentReports();
        reports.attachReporter(sparkReporter);
        reports.setSystemInfo("Environment", System.getProperty("env", "qa"));
        reports.setSystemInfo("OS", System.getProperty("os.name"));

        logger.info("ExtentReports initialized, writing to '{}'", reportPath);
        return reports;
    }

    public static synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTestThreadLocal.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }

    public static void unload() {
        extentTestThreadLocal.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed to disk.");
        }
    }
}
