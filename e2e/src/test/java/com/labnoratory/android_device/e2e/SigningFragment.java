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

public class SigningFragment {

    private static final By removeKeyButtonSelector = AppiumBy.id("removeKeyButton");
    private static final By titleSelector = AppiumBy.id("title");

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public static WebElement getAuthenticationRequired(WebDriver driver) {
        return driver.findElement(AppiumBy.id("authenticationRequired"));
    }

    public static WebElement getCreateKeyButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("createKeyButton"));
    }

    public static WebElement getInputElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("input"));
    }

    public static WebElement getPublicKeyElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("publicKey"));
    }

    public static WebElement getRemoveKeyButton(WebDriver driver) {
        return driver.findElement(removeKeyButtonSelector);
    }

    public static WebElement getSignatureElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("signature"));
    }

    public static WebElement getSignButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("signButton"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public static WebElement getVerifySignatureButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("verifySignatureButton"));
    }

    public SigningFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SigningFragment assertPublicKey(Matcher<String> matcher) {
        assertText(driver, SigningFragment::getPublicKeyElement, matcher);
        return this;
    }

    public SigningFragment assertSignature(Matcher<String> matcher) {
        assertText(driver, SigningFragment::getSignatureElement, matcher);
        return this;
    }

    public SigningFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, SigningFragment::getStatusElement, matcher);
        return this;
    }

    public SigningFragment assertStatus(Matcher<String> matcher, String errorMessage) {
        assertText(driver, SigningFragment::getStatusElement, matcher, errorMessage);
        return this;
    }

    public SigningFragment assertBiometricPromptDisplayed() {
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public SigningFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (matchesPattern(".*NO.*").matches(authenticationRequired.getText())) {
            authenticationRequired.click();
        }
        assertText(driver, SigningFragment::getAuthenticationRequired, matchesPattern(".*OFF.*"));
        return this;
    }

    public SigningFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = getAuthenticationRequired(driver);
        if (matchesPattern(".*OFF.*").matches(authenticationRequired.getText())) {
            authenticationRequired.click();
        }
        assertText(driver, SigningFragment::getAuthenticationRequired, matchesPattern(".*ON.*"));
        return this;
    }

    public SigningFragment cancelBiometrics() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .cancel();
        return this;
    }

    public SigningFragment clickCreateKeyButton() {
        getCreateKeyButton(driver).click();
        return this;
    }

    public SigningFragment clickRemoveKeyButton() {
        getRemoveKeyButton(driver).click();
        return this;
    }

    public SigningFragment clickVerifySignatureButton() {
        getVerifySignatureButton(driver).click();
        return this;
    }

    public SigningFragment clickSignButton() {
        getSignButton(driver).click();
        return this;
    }

    public SigningFragment createKey() {
        return clickCreateKeyButton().assertStatus(is(equalTo("Signing key created successfully")));
    }

    public String getSignature() {
        return getSignatureElement(driver).getText();
    }

    public String getPublicKey() {
        return getPublicKeyElement(driver).getText();
    }

    public boolean isKeyAvailable() {
        return !driver.findElements(removeKeyButtonSelector).isEmpty();
    }

    public SigningFragment removeKey() {
        return clickRemoveKeyButton().assertStatus(is(equalTo("Key removed successfully")));
    }

    public SigningFragment scanEnrolledFinger() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .scanEnrolledFinger();
        return this;
    }

    public SigningFragment setSignature(CharSequence... text) {
        setText(getSignatureElement(driver), text);
        return this;
    }

    public SigningFragment setInput(CharSequence... text) {
        setText(getInputElement(driver), text);
        return this;
    }

    public SigningFragment setPublicKey(CharSequence... text) {
        setText(getPublicKeyElement(driver), text);
        return this;
    }

    public SigningFragment waitUntilDisplayed() {
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
