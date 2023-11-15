package com.labnoratory.android_device.e2e;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.byId;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AuthenticationFragment {

    private static class Selectors {
        private static final By authenticateButton = byId("authenticateButton");
        private static final By title = byId("title");
        private static final By clearButton = byId("clearButton");
        private static final By status = byId("status");
    }

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public AuthenticationFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public AuthenticationFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, Selectors.status, matcher);
        return this;
    }

    public AuthenticationFragment cancelBiometricAuthentication() {
        getBiometricPromptFragment().cancel();
        return this;
    }

    public AuthenticationFragment clickAuthenticateButton() {
        driver.findElement(Selectors.authenticateButton).click();
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public AuthenticationFragment clickClearButton() {
        driver.findElement(Selectors.clearButton).click();
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
        assertText(driver, Selectors.title, is(equalTo("Authenticate")));
        return this;
    }

    private BiometricPromptFragment getBiometricPromptFragment() {
        if (null == biometricPromptFragment) {
            biometricPromptFragment = new BiometricPromptFragment(driver);
        }
        return biometricPromptFragment;
    }
}
