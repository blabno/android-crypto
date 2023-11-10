package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;
import static com.labnoratory.android_device.e2e.SecuritySettingsFragment.resourceId;

public class SymmetricEncryptionWithPasswordFragment {

    private static final By titleSelector = AppiumBy.id("title");

    private final AndroidDriver driver;

    public static WebElement getCipherTextElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("cipherText"));
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
        String uiautomatorText = String.format("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(0))", resourceId("decryptButton"));
        driver.findElement(AppiumBy.androidUIAutomator(uiautomatorText))
                .click();
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment clickEncryptButton() {
        String uiautomatorText = String.format("new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().resourceId(\"%s\").instance(0))", resourceId("encryptButton"));
        driver.findElement(AppiumBy.androidUIAutomator(uiautomatorText))
                .click();
        return this;
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public String getIv() {
        return getIvElement(driver).getText();
    }

    public SymmetricEncryptionWithPasswordFragment setCipherText(CharSequence... text) {
        setText(getCipherTextElement(driver), text);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setInput(CharSequence... text) {
        setText(getInputElement(driver), text);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setIterations(CharSequence... text) {
        setText(getIterationsElement(driver), text);
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public SymmetricEncryptionWithPasswordFragment setIv(CharSequence... text) {
        setText(getIvElement(driver), text);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setPassword(CharSequence... text) {
        setText(getPasswordElement(driver), text);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment setSalt(CharSequence... text) {
        setText(getSaltElement(driver), text);
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public SymmetricEncryptionWithPasswordFragment waitUntilDisplayed() {
        FragmentHelper.waitUntilDisplayed(driver, titleSelector);
        return this;
    }
}
