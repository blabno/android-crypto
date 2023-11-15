package com.labnoratory.android_device.e2e;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.byId;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;

public class SymmetricEncryptionWithPasswordFragment {

    private static final By titleSelector = AppiumBy.id("title");

    private final AndroidDriver driver;

    public static WebElement getCipherTextElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("cipherText"));
    }

    public static WebElement getInputElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("input"));
    }

    public static WebElement getIVElement(WebDriver driver) {
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

    public SymmetricEncryptionWithPasswordFragment assertCipherText(Matcher<String> matcher) {
        assertText(driver, SymmetricEncryptionWithPasswordFragment::getCipherTextElement, matcher);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment assertIV(Matcher<String> matcher) {
        assertText(driver, SymmetricEncryptionWithPasswordFragment::getIVElement, matcher);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, SymmetricEncryptionWithPasswordFragment::getStatusElement, matcher);
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment clickDecryptButton() {
        driver.findElement(byId("decryptButton")).click();
        return this;
    }

    public SymmetricEncryptionWithPasswordFragment clickEncryptButton() {
        driver.findElement(byId("encryptButton")).click();
        return this;
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public String getIV() {
        return getIVElement(driver).getText();
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

    /** @noinspection UnusedReturnValue*/
    public SymmetricEncryptionWithPasswordFragment setIV(CharSequence... text) {
        setText(getIVElement(driver), text);
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

    public SymmetricEncryptionWithPasswordFragment waitUntilDisplayed() {
        FragmentHelper.waitUntilDisplayed(driver, titleSelector);
        return this;
    }
}
