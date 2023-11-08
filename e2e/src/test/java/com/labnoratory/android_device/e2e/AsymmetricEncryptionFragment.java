package com.labnoratory.android_device.e2e;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;

public class AsymmetricEncryptionFragment {

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

    public static WebElement getRemoveKeyButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("removeKeyButton"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public AsymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public AsymmetricEncryptionFragment assertStatus(String pattern) {
        assertText(driver, AsymmetricEncryptionFragment::getStatusElement, pattern);
        return this;
    }

    public AsymmetricEncryptionFragment setInput(CharSequence... keysToSend) {
        WebElement element = getInputElement(driver);
        element.clear();
        element.sendKeys(keysToSend);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public AsymmetricEncryptionFragment clickCreateKeyButton() {
        getCreateKeyButton(driver).click();
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public AsymmetricEncryptionFragment clickRemoveKeyButton() {
        getRemoveKeyButton(driver).click();
        return this;
    }

    public AsymmetricEncryptionFragment clickDecryptButton() {
        getDecryptButton(driver).click();
        return this;
    }

    public AsymmetricEncryptionFragment clickEncryptButton() {
        getEncryptButton(driver).click();
        return this;
    }

    public AsymmetricEncryptionFragment createKey() {
        clickCreateKeyButton();
        assertStatus("Encryption key created successfully");
        return this;
    }

    public AsymmetricEncryptionFragment removeKey() {
        clickRemoveKeyButton();
        assertStatus("Key removed successfully");
        return this;
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

}
