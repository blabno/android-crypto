package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;

public class SymmetricEncryptionFragment {

    private static final By removeKeyButtonSelector = AppiumBy.id("removeKeyButton");

    private final AndroidDriver driver;

    public static WebElement getCipherTextElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("cipherText"));
    }

    public static WebElement getCreateKeyButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("createKeyButton"));
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

    public static WebElement getRemoveKeyButton(WebDriver driver) {
        return driver.findElement(removeKeyButtonSelector);
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public SymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SymmetricEncryptionFragment assertStatus(String pattern) {
        assertText(driver, SymmetricEncryptionFragment::getStatusElement, pattern);
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public SymmetricEncryptionFragment clickCreateKeyButton() {
        getCreateKeyButton(driver).click();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public SymmetricEncryptionFragment clickRemoveKeyButton() {
        getRemoveKeyButton(driver).click();
        return this;
    }

    public SymmetricEncryptionFragment clickDecryptButton() {
        getDecryptButton(driver).click();
        return this;
    }

    public SymmetricEncryptionFragment clickEncryptButton() {
        getEncryptButton(driver).click();
        return this;
    }

    public SymmetricEncryptionFragment createKey() {
        clickCreateKeyButton();
        assertStatus("Encryption key created successfully");
        return this;
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public String getIv() {
        return getIvElement(driver).getText();
    }

    public boolean isKeyAvailable() {
        return !driver.findElements(removeKeyButtonSelector).isEmpty();
    }

    public SymmetricEncryptionFragment removeKey() {
        clickRemoveKeyButton();
        assertStatus("Key removed successfully");
        return this;
    }

    public SymmetricEncryptionFragment setCipherText(CharSequence... keysToSend) {
        WebElement element = getCipherTextElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    public SymmetricEncryptionFragment setInput(CharSequence... keysToSend) {
        WebElement element = getInputElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SymmetricEncryptionFragment setIv(CharSequence... keysToSend) {
        WebElement element = getIvElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }
}
