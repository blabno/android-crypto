package com.labnoratory.android_device.e2e;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

public class AsymmetricEncryptionFragment {

    private static final By createKeyButtonSelector = AppiumBy.id("createKeyButton");
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
        return driver.findElement(createKeyButtonSelector);
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
        return driver.findElement(removeKeyButtonSelector);
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public AsymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public AsymmetricEncryptionFragment assertCipherText(Matcher<String> matcher) {
        assertText(driver, AsymmetricEncryptionFragment::getCipherTextElement, matcher);
        return this;
    }

    public AsymmetricEncryptionFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, AsymmetricEncryptionFragment::getStatusElement, matcher);
        return this;
    }

    public AsymmetricEncryptionFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (authenticationRequired.getText().matches(".*ON.*")) {
            authenticationRequired.click();
        }
        assertText(driver, AsymmetricEncryptionFragment::getAuthenticationRequired, matchesPattern(".*OFF.*"));
        return this;
    }

    public AsymmetricEncryptionFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (authenticationRequired.getText().matches(".*OFF.*")) {
            authenticationRequired.click();
        }
        assertText(driver, AsymmetricEncryptionFragment::getAuthenticationRequired, matchesPattern(".*ON.*"));
        return this;
    }

    public AsymmetricEncryptionFragment cancelBiometrics() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .cancel();
        return this;
    }

    public AsymmetricEncryptionFragment clickCreateKeyButton() {
        getCreateKeyButton(driver).click();
        return this;
    }

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
        return clickCreateKeyButton()
                .assertStatus(is(equalTo("Encryption key created successfully")));
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public boolean isKeyAvailable() {
        return !driver.findElements(removeKeyButtonSelector).isEmpty();
    }

    public AsymmetricEncryptionFragment removeKey() {
        return clickRemoveKeyButton()
                .assertStatus(is(equalTo("Key removed successfully")));
    }

    public AsymmetricEncryptionFragment scanEnrolledFinger() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .scanEnrolledFinger();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public AsymmetricEncryptionFragment setCipherText(CharSequence... text) {
        setText(getCipherTextElement(driver), text);
        return this;
    }

    public AsymmetricEncryptionFragment setInput(CharSequence... text) {
        setText(getInputElement(driver), text);
        return this;
    }

    public AsymmetricEncryptionFragment waitUntilDisplayed() {
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
