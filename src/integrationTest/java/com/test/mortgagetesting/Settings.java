package com.test.mortgagetesting;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class Settings {

    enum DriverType {
        CHROME,
        FIREFOX
    }

    private static final String PROPERTY_DRIVER_TYPE = "webdriver.type";
    private static final String PROPERTY_WEBDRIVER_PATH = "webdriver.path";

    private static final String SYSTEM_PROPERTY_CHROME = "webdriver.chrome.driver";
    private static final String SYSTEM_PROPERTY_FIREFOX = "webdriver.gecko.driver";

    private Properties properties;
    private DriverType driverType;

    public Settings(String propertiesFileName) {
        try {
            properties = new Properties();
            properties.load(Files.newInputStream(Util.getResourceFilePath(propertiesFileName)));
            processSettings();
        } catch (IOException e) {
            // There is no way to recover from error when reading properties file.
            // Handling this exception does not make sense here.
            // Converting to unchecked exception.
            throw new RuntimeException(e);
        }
    }

    private void processSettings() {
        String driverTypeProperty = properties.getProperty(PROPERTY_DRIVER_TYPE);
        String driverPathProperty = properties.getProperty(PROPERTY_WEBDRIVER_PATH);

        if (driverTypeProperty == null) {
            throw new RuntimeException("Web Driver Type not set");
        }
        if (driverPathProperty == null) {
            throw new RuntimeException("Web driver path not set");
        }

        driverType = DriverType.valueOf(driverTypeProperty.toUpperCase());
        switch (driverType) {
            case CHROME:
                System.setProperty(SYSTEM_PROPERTY_CHROME, properties.getProperty(PROPERTY_WEBDRIVER_PATH));
                break;
            case FIREFOX:
                System.setProperty(SYSTEM_PROPERTY_FIREFOX, properties.getProperty(PROPERTY_WEBDRIVER_PATH));
                break;
        }

        File driverExecutable = new File(driverPathProperty);
        if (!driverExecutable.exists()) {
            throw new RuntimeException("Web driver not found on specified path: " + driverPathProperty);
        }
    }

    public WebDriver getWebDriver() {
        switch (driverType) {
            case FIREFOX:
                return new FirefoxDriver();
            case CHROME:
                return new ChromeDriver();
            default:
                return null; // this never occurs as driverType is checked when creating class instance
        }

    }
}
