package com.labnoratory.android_device.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;

public class SymmetricEncryptionWithPasswordFragment {

    private final AndroidDriver driver;

    public static WebElement getCipherTextElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("cipherText"));
    }

    public static WebElement getDecryptButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("decryptButton"));
    }

    public static WebElement getEncryptButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("encryptButton"));
    }

    public static WebElement getInputElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("input"));
    }

    public static WebElement getIvElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("iv"));
    }

    public static WebElement getIterationsElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("iterations"));
    }

    public static WebElement getPasswordElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("password"));
    }

    public static WebElement getSaltElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("salt"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public SymmetricEncryptionWithPasswordFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SymmetricEncryptionWithPasswordFragment assertStatus(String pattern) {
        assertText(driver, SymmetricEncryptionWithPasswordFragment::getStatusElement, pattern);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment clickDecryptButton() {
        getDecryptButton(driver).click();
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment clickEncryptButton() {
        getEncryptButton(driver).click();
        return this;
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public String getIv() {
        return getIvElement(driver).getText();
    }

    public SymmetricEncryptionWithPasswordFragment setCipherText(CharSequence... keysToSend) {
        WebElement element = getCipherTextElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setInput(CharSequence... keysToSend) {
        WebElement element = getInputElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setIterations(CharSequence... keysToSend) {
        WebElement element = getIterationsElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SymmetricEncryptionWithPasswordFragment setIv(CharSequence... keysToSend) {
        WebElement element = getIvElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setPassword(CharSequence... keysToSend) {
        WebElement element = getPasswordElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setSalt(CharSequence... keysToSend) {
        WebElement element = getSaltElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }
}
