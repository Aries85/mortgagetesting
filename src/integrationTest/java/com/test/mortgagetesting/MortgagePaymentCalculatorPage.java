package com.test.mortgagetesting;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Page object. It contains all logic for operations on page.
 *
 * XPaths and titles could be externalized into property file, however for the sake of simplicity it has not been done here.
 *
 * Private methods could be probably extracted to separate class or ancestor,
 * however with just one page to test, it would not be beneficial for other use cases.
 */
public class MortgagePaymentCalculatorPage {

    private static final int WAIT_TIME_FOR_ELEMENT = 5;

    private static final String XPATH_CALCULATOR_TITLE = "//span[@class='calculator-title']";
    private static final String XPATH_FORM = "./parent::div";
    private static final String XPATH_FIELD_BY_LABEL_NAME = ".//label[text()='%s']/following-sibling::div/input";
    private static final String STEP2_XPATH_FIELD_BY_LABEL_NAME = "//label[text()='%s']/following-sibling::input"; // Dirty solution for values on second page. in real world project I would investigate more.
    private static final String XPATH_STEP2_BUTTON = "//a[@class='calculator-button next-button']";
    private static final String XPATH_FINISH_BUTTON = "//li[@class='next finish']/a[@class='calculator-button finish-button']"; // Matching by parent li is necessary as well, there are multiple matching a elements
    private static final String XPATH_RESULTS_TABLE = "//div[@id='analysisDiv']";
    private static final String XPATH_RESULT_ROW_BY_NAME = ".//th[text()='%s']/following-sibling::td";

    private static final String TITLE_FORM = "How Much Will My Payments Be?";
    public static final String TITLE_FIELD_LOAN_AMOUNT = "Loan Amount";
    public static final String TITLE_FIELD_INTEREST_RATE = "Interest Rate";
    public static final String TITLE_FIELD_LENGTH = "Length";
    public static final String TITLE_FIELD_HOME_VALUE = "Home Value";
    public static final String TITLE_FIELD_ANNUAL_TAXES = "Annual Taxes";
    public static final String TITLE_FIELD_ANNUAL_INSURANCE = "Annual Insurance";
    public static final String TITLE_FIELD_ANNUAL_PMI = "Annual PMI";

    public static final String TITLE_RESULT_MONTHLY_PRINCIPALS_AND_INTERESTS = "Monthly Principal & Interests";
    public static final String TITLE_RESULT_MONTHLY_REAL_ESTATE_TAXES = "Monthly Real Estate Taxes";
    public static final String TITLE_RESULT_MONTHLY_INSURANCE = "Monthly Insurance";
    public static final String TITLE_RESULT_LOAD_TO_VALUE_RATIO = "Loan To Value Ratio";
    public static final String TITLE_RESULT_MONTHS_WITH_PMI = "Months With PMI";
    public static final String TITLE_RESULT_MONTHLY_PMI = "Monthly PMI";
    public static final String TITLE_RESULT_TOTAL_MONTHLY_PAYMENTS = "Total Monthly Payments";

    public static final String MORTGAGE_CALCULATOR_URL = "https://www.mortgageloan.com/calculator";
    private WebElement formElement;
    private boolean onStep2 = false;
    private WebDriver driver;

    public MortgagePaymentCalculatorPage(WebDriver driver) {
        this.driver = driver;
        // sanity check
        if (!driver.getCurrentUrl().startsWith(MORTGAGE_CALCULATOR_URL)) {
            throw new RuntimeException("Wrong page is opened in driver. Expected: " + MORTGAGE_CALCULATOR_URL + " Actual: " + driver.getCurrentUrl());
        }
        final WebElement titleElement = driver.findElement(By.xpath(XPATH_CALCULATOR_TITLE));
        final String currentCalculatorTitle = titleElement.getText();
        if (!TITLE_FORM.equals(currentCalculatorTitle)) {
            throw new RuntimeException("Wrong calculator title is opened on page. Expected: \"" + TITLE_FORM + "\" Actual: \"" + currentCalculatorTitle + "\"");
        };
        formElement = findSingleElement(titleElement, XPATH_FORM);
    }

    public void setFieldValueByLabel(String label, String value) {
        final WebElement inputElement;
        if (!onStep2) {
            inputElement = formElement.findElement(By.xpath(String.format(XPATH_FIELD_BY_LABEL_NAME, label)));
        } else {
            inputElement = formElement.findElement(By.xpath(String.format(STEP2_XPATH_FIELD_BY_LABEL_NAME, label)));
        }
        inputElement.clear();
        inputElement.sendKeys(value);
    }

    public String getResultByLabel(String label) {
        WebElement resultsTableElement = driver.findElement(By.xpath(XPATH_RESULTS_TABLE));
        final WebElement resultValueElement = resultsTableElement.findElement(By.xpath(String.format(XPATH_RESULT_ROW_BY_NAME, label)));
        return resultValueElement.getText();
    }

    public void clickNextButton() {
        clickElementByXpath(XPATH_STEP2_BUTTON);
        onStep2 = true;
    }

    public void clickShowResultsButton() {
        clickElementByXpath(XPATH_FINISH_BUTTON);
    }

    private void clickElementByXpath(String xpath) {
        final WebElement linkElement = formElement.findElement(By.xpath(xpath));
        linkElement.click();
    }

    /**
     * Helper function for test stability and easy development.
     *
     * To prevent accidental test failures, before each element search wait is performed to ensure that element has
     * been already displayed. detection of analysisDiv results table fails occasionally.
     *
     * If there is more than one element matching XPath, it can cause errors saying element is not visible. To prevent
     * such errors, search is always performed for multiple elements. If more than one matching element is found,
     * exception is thrown.
     *
     * Source: https://stackoverflow.com/questions/6101461/how-to-force-selenium-webdriver-to-click-on-element-which-is-not-currently-visib
     *  @param element element to search on
     * @param elementXPath XPath expression
     */
    private WebElement findSingleElement(@Nonnull WebElement element, @Nonnull String elementXPath) {
        /* Selenium's waiting for element does not work for XPath selector ./parent::div,
         * probably applies to all parent selectors. In real world scenario, more investigation with
         * sample test cases would be done.
         * In this case, just workaround will be done.
         */
        if (!elementXPath.contains("parent")) {
            WebDriverWait wait = new WebDriverWait(driver, WAIT_TIME_FOR_ELEMENT);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(elementXPath)));
        }
        final List<WebElement> foundElements = element.findElements(By.xpath(elementXPath));
        if (foundElements.size() > 1) {
            throw new RuntimeException(String.format("Search for single element returned %d results. Please ensure uniqueness of XPath expression.", foundElements.size()));
        }
        return foundElements.get(0);
    }

}
