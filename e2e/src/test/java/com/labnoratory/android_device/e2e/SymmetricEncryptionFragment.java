package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;

public class SymmetricEncryptionFragment {

    private static final By removeKeyButtonSelector = AppiumBy.id("removeKeyButton");
    private static final By titleSelector = AppiumBy.id("title");

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public static WebElement getAuthenticationRequired(WebDriver driver) {
        return driver.findElement(AppiumBy.id("authenticationRequired"));
    }

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

    public SymmetricEncryptionFragment assertBiometricPromptDisplayed() {
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public SymmetricEncryptionFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (authenticationRequired.getText().matches(".*ON.*")) {
            authenticationRequired.click();
        }
        assertText(driver, SymmetricEncryptionFragment::getAuthenticationRequired, ".*OFF.*");
        return this;
    }

    public SymmetricEncryptionFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (authenticationRequired.getText().matches(".*OFF.*")) {
            authenticationRequired.click();
        }
        assertText(driver, SymmetricEncryptionFragment::getAuthenticationRequired, ".*ON.*");
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

    public SymmetricEncryptionFragment scanEnrolledFinger() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .scanEnrolledFinger();
        return this;
    }

    public SymmetricEncryptionFragment setCipherText(CharSequence... text) {
        setText(getCipherTextElement(driver), text);
        return this;
    }

    public SymmetricEncryptionFragment setInput(CharSequence... text) {
        setText(getInputElement(driver), text);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SymmetricEncryptionFragment setIv(CharSequence... text) {
        setText(getIvElement(driver), text);
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SymmetricEncryptionFragment waitUntilDisplayed() {
        FragmentHelper.waitUntilDisplayed(driver, titleSelector);
        return this;
    }

    private BiometricPromptFragment getBiometricPromptFragment() {
        if (null == biometricPromptFragment) {
            biometricPromptFragment = new BiometricPromptFragment(driver);
        }
        return biometricPromptFragment;
    }
}
