package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;

public class AuthenticationFragment {

    private static final By titleSelector = AppiumBy.id("title");

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public static WebElement getAuthenticateButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("authenticateButton"));
    }

    public static WebElement getClearButton(WebDriver driver) {
        return driver.findElement(AppiumBy.id("clearButton"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(AppiumBy.id("status"));
    }

    public AuthenticationFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    /** @noinspection UnusedReturnValue*/
    public AuthenticationFragment assertStatus(String pattern) {
        assertText(driver, AuthenticationFragment::getStatusElement, pattern);
        return this;
    }

    public AuthenticationFragment cancelBiometricAuthentication() {
        getBiometricPromptFragment().cancel();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public AuthenticationFragment clickAuthenticateButton() {
        getAuthenticateButton(driver).click();
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public AuthenticationFragment clickClearButton() {
        getClearButton(driver).click();
        assertStatus("");
        return this;
    }

    public AuthenticationFragment scanEnrolledFinger() {
        getBiometricPromptFragment().scanEnrolledFinger();
        return this;
    }

    public AuthenticationFragment scanUnknownFinger() {
        getBiometricPromptFragment().scanUnknownFinger();
        return this;
    }

    public AuthenticationFragment waitUntilDisplayed() {
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
