import com.test.mortgagetesting.MortgagePaymentCalculatorPage;
import com.test.mortgagetesting.Settings;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.*;
import org.openqa.selenium.WebDriver;

import static com.test.mortgagetesting.MortgagePaymentCalculatorPage.*;


public class MortgagePaymentCalculatorTest {

    @Rule
    public final JUnitSoftAssertions softAssertions = new JUnitSoftAssertions();

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        Settings settings = new Settings("test-settings.properties");
        driver = settings.getWebDriver();
    }

    @Test
    public void testMortgagePaymentCalculator() {
        driver.get(MortgagePaymentCalculatorPage.MORTGAGE_CALCULATOR_URL);
        final MortgagePaymentCalculatorPage page = new MortgagePaymentCalculatorPage(driver);
        page.setFieldValueByLabel(TITLE_FIELD_LOAN_AMOUNT, "200,000");
        page.setFieldValueByLabel(TITLE_FIELD_INTEREST_RATE, "5.0");
        page.setFieldValueByLabel(TITLE_FIELD_LENGTH, "30");
        page.setFieldValueByLabel(TITLE_FIELD_HOME_VALUE, "235,000");
        page.clickNextButton();
        page.setFieldValueByLabel(TITLE_FIELD_ANNUAL_TAXES, "2,000");
        page.setFieldValueByLabel(TITLE_FIELD_ANNUAL_INSURANCE, "1,865");
        page.setFieldValueByLabel(TITLE_FIELD_ANNUAL_PMI, "0.52");
        page.clickShowResultsButton();

        /* Value formatting could be done by page class. However this way it is tested that formatting works correctly.
           In real scenario, there should be few tests for formatting and other tests would use number formatting
           by page class.
           This would reduce work in case formatting rules changed. */
        softAssertions.assertThat(page.getResultByLabel(TITLE_RESULT_MONTHLY_PRINCIPALS_AND_INTERESTS)).isEqualTo("$1,073.64");
        softAssertions.assertThat(page.getResultByLabel(TITLE_RESULT_LOAD_TO_VALUE_RATIO)).isEqualTo("85.11%");
        softAssertions.assertThat(page.getResultByLabel(TITLE_RESULT_TOTAL_MONTHLY_PAYMENTS)).isEqualTo("$1,482.39");
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }

}
