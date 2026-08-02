package com.qa.framework.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a timestamped screenshot for a given driver and returns the saved file path.
 * Used by TestListener on test failure.
 */
public class ScreenshotUtils {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "screenshots";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    public static String captureScreenshot(WebDriver driver, String testName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String fileName = testName + "_" + timestamp + ".png";

        try {
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = Paths.get(SCREENSHOT_DIR, fileName).toFile();
            FileUtils.copyFile(srcFile, destFile);

            logger.info("Screenshot captured for test '{}' at '{}'", testName, destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Failed to capture screenshot for test '{}'", testName, e);
            return null;
        }
    }
}
