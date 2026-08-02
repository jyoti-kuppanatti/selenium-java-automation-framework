package com.qa.framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Thread-safe singleton that loads framework configuration once per JVM run.
 * Config file is selected via -Denv=<env> (defaults to "qa"), e.g. config-qa.properties.
 * Falls back to config.properties for values missing in the env-specific file.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    private static volatile ConfigReader instance;
    private final Properties properties = new Properties();

    private ConfigReader() {
        loadProperties("config.properties");

        String env = System.getProperty("env", "qa");
        String envFile = "config-" + env + ".properties";
        loadProperties(envFile);

        logger.info("ConfigReader initialized for env='{}'", env);
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    private void loadProperties(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                logger.warn("Config file '{}' not found on classpath, skipping.", fileName);
                return;
            }
            Properties fileProps = new Properties();
            fileProps.load(input);
            properties.putAll(fileProps);
            logger.info("Loaded config file '{}'", fileName);
        } catch (IOException e) {
            logger.error("Failed to load config file '{}'", fileName, e);
            throw new RuntimeException("Failed to load config file: " + fileName, e);
        }
    }

    public String get(String key) {
        String systemOverride = System.getProperty(key);
        if (systemOverride != null && !systemOverride.isBlank()) {
            return systemOverride;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing config property: " + key);
        }
        return value;
    }

    public String get(String key, String defaultValue) {
        String systemOverride = System.getProperty(key);
        if (systemOverride != null && !systemOverride.isBlank()) {
            return systemOverride;
        }
        return properties.getProperty(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
    }

    public int getInt(String key, int defaultValue) {
        return Integer.parseInt(get(key, String.valueOf(defaultValue)));
    }
}
