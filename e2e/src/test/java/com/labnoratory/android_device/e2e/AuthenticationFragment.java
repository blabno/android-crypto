package com.labnoratory.android_device.e2e;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.byId;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

public class AuthenticationFragment {

    private static final By titleSelector = byId("title");

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public static WebElement getAuthenticateButton(WebDriver driver) {
        return driver.findElement(byId("authenticateButton"));
    }

    public static WebElement getClearButton(WebDriver driver) {
        return driver.findElement(byId("clearButton"));
    }

    public static WebElement getStatusElement(WebDriver driver) {
        return driver.findElement(byId("status"));
    }

    public AuthenticationFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public AuthenticationFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, AuthenticationFragment::getStatusElement, matcher);
        return this;
    }

    public AuthenticationFragment cancelBiometricAuthentication() {
        getBiometricPromptFragment().cancel();
        return this;
    }

    public AuthenticationFragment clickAuthenticateButton() {
        getAuthenticateButton(driver).click();
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public AuthenticationFragment clickClearButton() {
        getClearButton(driver).click();
        return assertStatus(is(emptyString()));
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
