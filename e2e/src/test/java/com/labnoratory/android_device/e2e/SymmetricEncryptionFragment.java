package com.labnoratory.android_device.e2e;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.byId;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

public class SymmetricEncryptionFragment {

    private static final By removeKeyButtonSelector = byId("removeKeyButton");
    private static final By titleSelector = byId("title");

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public static WebElement getAuthenticationRequired(WebDriver driver) {
        return driver.findElement(byId("authenticationRequired"));
    }

    public static WebElement getCipherTextElement(WebDriver driver) {
        return driver.findElement(byId("cipherText"));
    }

    public static WebElement getCreateKeyButton(WebDriver driver) {
        return driver.findElement(byId("createKeyButton"));
    }

    public static WebElement getDecryptButton(WebDriver driver) {
        return driver.findElement(byId("decryptButton"));
    }

    public static WebElement getEncryptButton(WebDriver driver) {
        return driver.findElement(byId("encryptButton"));
    }

    public static WebElement getInputElement(WebDriver driver) {
        return driver.findElement(byId("input"));
    }

    public static WebElement getIVElement(WebDriver driver) {
        return driver.findElement(byId("iv"));
    }

    public static WebElement getRemoveKeyButton(WebDriver driver) {
        return driver.findElement(removeKeyButtonSelector);
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(byId("status"));
    }

    public SymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SymmetricEncryptionFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, SymmetricEncryptionFragment::getStatusElement, matcher);
        return this;
    }

    public SymmetricEncryptionFragment assertBiometricPromptDisplayed() {
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public SymmetricEncryptionFragment assertCipherText(Matcher<String> matcher) {
        assertText(driver, SymmetricEncryptionFragment::getCipherTextElement, matcher);
        return this;
    }

    public SymmetricEncryptionFragment assertIV(Matcher<String> matcher) {
        assertText(driver, SymmetricEncryptionFragment::getIVElement, matcher);
        return this;
    }

    public SymmetricEncryptionFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (authenticationRequired.getText().matches(".*ON.*")) {
            authenticationRequired.click();
        }
        assertText(driver, SymmetricEncryptionFragment::getAuthenticationRequired, matchesPattern(".*OFF.*"));
        return this;
    }

    public SymmetricEncryptionFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (authenticationRequired.getText().matches(".*OFF.*")) {
            authenticationRequired.click();
        }
        assertText(driver, SymmetricEncryptionFragment::getAuthenticationRequired, matchesPattern(".*ON.*"));
        return this;
    }

    public SymmetricEncryptionFragment cancelBiometrics() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .cancel();
        return this;
    }

    public SymmetricEncryptionFragment clickCreateKeyButton() {
        getCreateKeyButton(driver).click();
        return this;
    }

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
        return clickCreateKeyButton().assertStatus(is(equalTo("Encryption key created successfully")));
    }

    public String getCipherText() {
        return getCipherTextElement(driver).getText();
    }

    public String getIv() {
        return getIVElement(driver).getText();
    }

    public boolean isKeyAvailable() {
        return !driver.findElements(removeKeyButtonSelector).isEmpty();
    }

    public SymmetricEncryptionFragment removeKey() {
        return clickRemoveKeyButton().assertStatus(is(equalTo("Key removed successfully")));
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

    /**
     * @noinspection UnusedReturnValue
     */
    public SymmetricEncryptionFragment setIV(CharSequence... text) {
        setText(getIVElement(driver), text);
        return this;
    }

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
