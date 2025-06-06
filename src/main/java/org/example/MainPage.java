package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

public class MainPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    // --------------- Локаторы ---------------

    /** Контейнер блока «Онлайн пополнение без комиссии» */
    @FindBy(id = "pay-section")
    private WebElement paySection;

    /** Заголовок блока «Онлайн пополнение без комиссии» */
    @FindBy(xpath = "//*[@id='pay-section']//h2")
    private WebElement blockName;

    /** Кнопка «Отклонить cookies» */
    @FindBy(css = "button.cookie__cancel[data-close]")
    private WebElement rejectCookiesButton;

    /** Ссылка «Подробнее о сервисе» */
    @FindBy(xpath = "//*[@id='pay-section']//a[@href='/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/']")
    private WebElement aboutServiceLink;

    /** Логотипы платёжных систем */
    @FindBy(xpath = "//*[@id='pay-section']//img[@alt='Visa']")
    private WebElement visaLogo;
    @FindBy(xpath = "//*[@id='pay-section']//img[@alt='Verified By Visa']")
    private WebElement verifiedVisaLogo;
    @FindBy(xpath = "//*[@id='pay-section']//img[@alt='MasterCard']")
    private WebElement masterCardLogo;
    @FindBy(xpath = "//*[@id='pay-section']//img[@alt='MasterCard Secure Code']")
    private WebElement masterCardSecureLogo;
    @FindBy(xpath = "//*[@id='pay-section']//img[@alt='Белкарт']")
    private WebElement belcardLogo;

    /** Поля «Услуги связи» */
    @FindBy(id = "connection-phone")
    private WebElement connectionPhoneInput;
    @FindBy(id = "connection-sum")
    private WebElement connectionSumInput;
    @FindBy(id = "connection-email")
    private WebElement connectionEmailInput;
    @FindBy(xpath = "//*[@id='pay-section']//form[1]//button[contains(text(), 'Продолжить')]")
    private WebElement continueButton;

    /** Поля «Домашний интернет» */
    @FindBy(id = "internet-account")
    private WebElement homeInternetAccountInput;
    @FindBy(id = "internet-sum")
    private WebElement homeInternetSumInput;

    /** Поля «Рассрочка» */
    @FindBy(id = "installment-contract")
    private WebElement installmentContractInput;
    @FindBy(id = "installment-sum")
    private WebElement installmentSumInput;

    /** Поля «Задолженность» */
    @FindBy(id = "debt-account")
    private WebElement debtAccountInput;
    @FindBy(id = "debt-sum")
    private WebElement debtSumInput;

    /** iframe модального окна оплаты */
    @FindBy(css = "iframe.bepaid-iframe")
    private WebElement paymentFrame;

    // --------------- Методы ---------------

    /**
     * Если появилось окно cookies — кликнуть «Отклонить» через JS,
     * чтобы избежать ClickInterceptedException.
     */
    public void clickRejectCookies() {
        try {
            WebElement btn = wait.until(ExpectedConditions.visibilityOf(rejectCookiesButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        } catch (TimeoutException ignored) {
            // Если окна нет — пропускаем.
        }
    }

    /**
     * Возвращает текст заголовка блока «Онлайн пополнение без комиссии».
     */
    public String getBlockNameText() {
        scrollToPaySection();
        return wait
                .until(ExpectedConditions.visibilityOf(blockName))
                .getText()
                .replace("\n", " ")
                .trim();
    }

    /**
     * Кликает по ссылке «Подробнее о сервисе».
     */
    public void clickAboutServiceLink() {
        scrollToPaySection();
        wait.until(ExpectedConditions.elementToBeClickable(aboutServiceLink)).click();
    }

    /** Проверка логотипов платёжных систем */
    public boolean isVisaLogoDisplayed() {
        scrollToPaySection();
        return wait.until(ExpectedConditions.visibilityOf(visaLogo)).isDisplayed();
    }
    public boolean isVerifiedVisaLogoDisplayed() {
        scrollToPaySection();
        return wait.until(ExpectedConditions.visibilityOf(verifiedVisaLogo)).isDisplayed();
    }
    public boolean isMasterCardLogoDisplayed() {
        scrollToPaySection();
        return wait.until(ExpectedConditions.visibilityOf(masterCardLogo)).isDisplayed();
    }
    public boolean isMasterCardSecureLogoDisplayed() {
        scrollToPaySection();
        return wait.until(ExpectedConditions.visibilityOf(masterCardSecureLogo)).isDisplayed();
    }
    public boolean isBelcardLogoDisplayed() {
        scrollToPaySection();
        return wait.until(ExpectedConditions.visibilityOf(belcardLogo)).isDisplayed();
    }

    /**
     * Открывает вкладку внутри блока «Онлайн пополнение без комиссии».
     * Тексты вкладок (точно): «Услуги связи», «Домашний интернет», «Рассрочка», «Задолженность».
     */
    public void openTab(String tabName) {
        scrollToPaySection();

        // Используем «contains» по тексту вкладки, чтобы охватить варианты с пробелами или скрытыми элементами
        By locator = By.xpath("//*[@id='pay-section']//button[contains(normalize-space(.), '" + tabName + "')]");
        WebElement tabButton = wait.until(ExpectedConditions.elementToBeClickable(locator));
        tabButton.click();

        // После клика ждём видимость характерного поля этой вкладки
        switch (tabName) {
            case "Услуги связи":
                wait.until(ExpectedConditions.visibilityOf(connectionPhoneInput));
                break;
            case "Домашний интернет":
                wait.until(ExpectedConditions.visibilityOf(homeInternetAccountInput));
                break;
            case "Рассрочка":
                wait.until(ExpectedConditions.visibilityOf(installmentContractInput));
                break;
            case "Задолженность":
                wait.until(ExpectedConditions.visibilityOf(debtAccountInput));
                break;
            default:
                // не должно быть сюда
        }
    }

    // --- Методы для полей «Услуги связи» ---

    public String getPhonePlaceholder() {
        return wait
                .until(ExpectedConditions.visibilityOf(connectionPhoneInput))
                .getAttribute("placeholder")
                .trim();
    }
    public String getSumPlaceholder() {
        return connectionSumInput.getAttribute("placeholder").trim();
    }
    public String getEmailPlaceholder() {
        return connectionEmailInput.getAttribute("placeholder").trim();
    }
    public void enterPhoneNumber(String phone) {
        wait.until(ExpectedConditions.visibilityOf(connectionPhoneInput)).sendKeys(phone);
    }
    public void enterSum(String sum) {
        wait.until(ExpectedConditions.visibilityOf(connectionSumInput)).sendKeys(sum);
    }
    public void enterEmail(String email) {
        wait.until(ExpectedConditions.visibilityOf(connectionEmailInput)).sendKeys(email);
    }
    public void clickContinueButton() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    // --- Методы для полей «Домашний интернет» ---

    public String getHomeInternetAccountPlaceholder() {
        return wait
                .until(ExpectedConditions.visibilityOf(homeInternetAccountInput))
                .getAttribute("placeholder")
                .trim();
    }
    public String getHomeInternetSumPlaceholder() {
        return homeInternetSumInput.getAttribute("placeholder").trim();
    }

    // --- Методы для полей «Рассрочка» ---

    public String getInstallmentContractPlaceholder() {
        return wait
                .until(ExpectedConditions.visibilityOf(installmentContractInput))
                .getAttribute("placeholder")
                .trim();
    }
    public String getInstallmentSumPlaceholder() {
        return installmentSumInput.getAttribute("placeholder").trim();
    }

    // --- Методы для полей «Задолженность» ---

    public String getDebtAccountPlaceholder() {
        return wait
                .until(ExpectedConditions.visibilityOf(debtAccountInput))
                .getAttribute("placeholder")
                .trim();
    }
    public String getDebtSumPlaceholder() {
        return debtSumInput.getAttribute("placeholder").trim();
    }

    // --- Методы для работы с iframe ---

    public void switchToPaymentFrame() {
        scrollToPaySection();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(paymentFrame));
    }
    public void switchBackToDefault() {
        driver.switchTo().defaultContent();
    }

    public String getCardPageText() {
        return wait
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'BYN')]")))
                .getText()
                .trim();
    }

    public String getCardNumberPlaceholderInFrame() {
        WebElement pan = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pan")));
        return pan.getAttribute("placeholder").trim();
    }
    public String getExpiryDatePlaceholderInFrame() {
        WebElement exp = driver.findElement(By.id("expiration"));
        return exp.getAttribute("placeholder").trim();
    }
    public String getCvvPlaceholderInFrame() {
        WebElement cvv = driver.findElement(By.id("cvc"));
        return cvv.getAttribute("placeholder").trim();
    }
    public boolean isVisaIconDisplayedInFrame() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img[alt='Visa']"))).isDisplayed();
    }
    public boolean isMasterCardIconDisplayedInFrame() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img[alt='MasterCard']"))).isDisplayed();
    }

    /** Прокрутка к блоку pay-section, чтобы элементы были в зоне видимости */
    private void scrollToPaySection() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", paySection);
    }
}
