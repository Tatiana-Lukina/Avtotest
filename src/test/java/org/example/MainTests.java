package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MainTests {
    private WebDriver driver;
    private MainPage mainPage;

    @BeforeAll
    public void beforeAll() {
        // Настройка WebDriverManager и создание ChromeDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        mainPage = new MainPage(driver);
    }

    @BeforeEach
    public void beforeEach() {
        driver.get("https://www.mts.by/");
        mainPage.clickRejectCookies();  // скрыть окно cookies, если появилось
    }

    @AfterAll
    public void afterAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 1. Проверка заголовка блока «Онлайн пополнение без комиссии»
     */
    @Test
    @DisplayName("1. Заголовок блока «Онлайн пополнение без комиссии»")
    public void testBlockName() {
        String actual = mainPage.getBlockNameText();
        assertEquals(
                "Онлайн пополнение без комиссии",
                actual,
                "Заголовок блока не совпадает с ожидаемым"
        );
    }

    /**
     * 2. Проверить наличие логотипов платёжных систем
     */
    @ParameterizedTest
    @ValueSource(strings = {"visa", "verified by visa", "mastercard", "mastercard secure", "белкарт"})
    @DisplayName("2. Логотипы платёжных систем отображаются")
    public void testPaymentLogosAreDisplayed(String logo) {
        switch (logo.toLowerCase()) {
            case "visa":
                assertTrue(mainPage.isVisaLogoDisplayed(), "Логотип Visa не найден");
                break;
            case "verified by visa":
                assertTrue(mainPage.isVerifiedVisaLogoDisplayed(), "Логотип Verified By Visa не найден");
                break;
            case "mastercard":
                assertTrue(mainPage.isMasterCardLogoDisplayed(), "Логотип MasterCard не найден");
                break;
            case "mastercard secure":
                assertTrue(mainPage.isMasterCardSecureLogoDisplayed(), "Логотип MasterCard Secure не найден");
                break;
            case "белкарт":
                assertTrue(mainPage.isBelcardLogoDisplayed(), "Логотип Белкарт не найден");
                break;
            default:
                fail("Неизвестный логотип: " + logo);
        }
    }

    /**
     * 3. Проверить работу ссылки «Подробнее о сервисе»
     */
    @Test
    @DisplayName("3. Ссылка «Подробнее о сервисе» ведёт на правильный URL")
    public void testClickAboutServiceLink() {
        mainPage.clickAboutServiceLink();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlToBe(
                        "https://www.mts.by/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/"
                ));
        assertEquals(
                "https://www.mts.by/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/",
                driver.getCurrentUrl(),
                "URL после клика не соответствует ожидаемому"
        );
    }

    /**
     * 4.a. Проверить плейсхолдеры в разделе «Услуги связи»
     */
    @Test
    @DisplayName("4.a. Плейсхолдеры «Услуги связи»")
    public void testCommunicationServicePlaceholders() {
        mainPage.openTab("Услуги связи");
        assertEquals("Номер телефона", mainPage.getPhonePlaceholder(), "Плейсхолдер телефона неверен");
        assertEquals("Сумма", mainPage.getSumPlaceholder(), "Плейсхолдер суммы неверен");
        assertEquals("E-mail для отправки чека", mainPage.getEmailPlaceholder(), "Плейсхолдер email неверен");
    }

    /**
     * 4.b. Проверить плейсхолдеры в разделе «Домашний интернет»
     */
    @Test
    @DisplayName("4.b. Плейсхолдеры «Домашний интернет»")
    public void testHomeInternetPlaceholders() {
        mainPage.openTab("Домашний интернет");
        assertEquals(
                "Номер абонента",
                mainPage.getHomeInternetAccountPlaceholder(),
                "Плейсхолдер лицевого счёта неверен"
        );
        assertEquals(
                "Сумма",
                mainPage.getHomeInternetSumPlaceholder(),
                "Плейсхолдер суммы неверен"
        );
    }

    /**
     * 4.c. Проверить плейсхолдеры в разделе «Рассрочка»
     */
    @Test
    @DisplayName("4.c. Плейсхолдеры «Рассрочка»")
    public void testInstallmentPlaceholders() {
        mainPage.openTab("Рассрочка");
        assertEquals(
                "Номер счета на 44",
                mainPage.getInstallmentContractPlaceholder(),
                "Плейсхолдер номера счёта неверен"
        );
        assertEquals(
                "Сумма",
                mainPage.getInstallmentSumPlaceholder(),
                "Плейсхолдер суммы неверен"
        );
    }

    /**
     * 4.d. Проверить плейсхолдеры в разделе «Задолженность»
     */
    @Test
    @DisplayName("4.d. Плейсхолдеры «Задолженность»")
    public void testDebtPlaceholders() {
        mainPage.openTab("Задолженность");
        assertEquals(
                "Номер счета на 2073",
                mainPage.getDebtAccountPlaceholder(),
                "Плейсхолдер лицевого счёта неверен"
        );
        assertEquals(
                "Сумма",
                mainPage.getDebtSumPlaceholder(),
                "Плейсхолдер суммы неверен"
        );
    }

    /**
     * 5. Заполнить поля раздела «Услуги связи», нажать «Продолжить» и проверить iframe:
     *    – отображение суммы («100.00 BYN»),
     *    – плейсхолдеры («Номер карты», «MM/ГГ», «CVV»),
     *    – наличие иконок Visa и MasterCard внутри iframe.
     */
    @Test
    @DisplayName("5. Проверка работы «Услуги связи» и модального окна оплаты")
    public void testPaymentModalDetails() {
        // Открыть «Услуги связи»
        mainPage.openTab("Услуги связи");

        // Ввести тестовые данные
        mainPage.enterPhoneNumber("297777777");
        mainPage.enterSum("100.00");
        mainPage.enterEmail("test@mail.com");
        mainPage.clickContinueButton();

        // Перейти в iframe модального окна
        mainPage.switchToPaymentFrame();

        // Проверить отображение суммы внутри iframe
        String frameText = mainPage.getCardPageText();
        assertTrue(
                frameText.contains("100.00 BYN"),
                "Сумма не отображается корректно в iframe"
        );

        // Плейсхолдеры внутри iframe
        assertEquals(
                "Номер карты",
                mainPage.getCardNumberPlaceholderInFrame(),
                "Плейсхолдер номера карты неверен"
        );
        assertEquals(
                "MM/ГГ",
                mainPage.getExpiryDatePlaceholderInFrame(),
                "Плейсхолдер даты истечения неверен"
        );
        assertEquals(
                "CVV",
                mainPage.getCvvPlaceholderInFrame(),
                "Плейсхолдер CVV неверен"
        );

        // Проверить иконки платёжных систем внутри iframe
        assertTrue(
                mainPage.isVisaIconDisplayedInFrame(),
                "Иконка Visa не отображается в iframe"
        );
        assertTrue(
                mainPage.isMasterCardIconDisplayedInFrame(),
                "Иконка MasterCard не отображается в iframe"
        );

        // Вернуться из iframe в основной контент
        mainPage.switchBackToDefault();
    }
}
