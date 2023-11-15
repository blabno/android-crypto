package com.labnoratory.android_device.e2e;

import org.hamcrest.Matcher;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;
import static com.labnoratory.android_device.e2e.FragmentHelper.byId;
import static com.labnoratory.android_device.e2e.FragmentHelper.isDisplayed;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

public class SigningFragment {

    private static class Selectors {
        private static final By authenticationRequired = byId("authenticationRequired");
        private static final By createKeyButton = byId("createKeyButton");
        private static final By input = byId("input");
        private static final By publicKey = byId("publicKey");
        private static final By removeKeyButton = byId("removeKeyButton");
        private static final By signature = byId("signature");
        private static final By signButton = byId("signButton");
        private static final By status = byId("status");
        private static final By title = byId("title");
        private static final By verifySignatureButton = byId("verifySignatureButton");
    }

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public SigningFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SigningFragment assertPublicKey(Matcher<String> matcher) {
        assertText(driver, Selectors.publicKey, matcher);
        return this;
    }

    public SigningFragment assertSignature(Matcher<String> matcher) {
        assertText(driver, Selectors.signature, matcher);
        return this;
    }

    public SigningFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, Selectors.status, matcher);
        return this;
    }

    public SigningFragment assertStatus(Matcher<String> matcher, String errorMessage) {
        assertText(driver, Selectors.status, matcher, errorMessage);
        return this;
    }

    public SigningFragment assertBiometricPromptDisplayed() {
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public SigningFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = driver.findElement(Selectors.authenticationRequired);
        Matcher<String> matcher = matchesPattern(".*ON.*");
        if (matcher.matches(authenticationRequired.getText())) {
            authenticationRequired.click();
        }
        assertText(driver, Selectors.authenticationRequired, not(matcher));
        return this;
    }

    public SigningFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = driver.findElement(Selectors.authenticationRequired);
        Matcher<String> matcher = matchesPattern(".*ON.*");
        if (not(matcher).matches(authenticationRequired.getText())) {
            authenticationRequired.click();
        }
        assertText(driver, Selectors.authenticationRequired, matcher);
        return this;
    }

    public SigningFragment cancelBiometrics() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .cancel();
        return this;
    }

    public SigningFragment clickCreateKeyButton() {
        driver.findElement(Selectors.createKeyButton).click();
        return this;
    }

    public SigningFragment clickRemoveKeyButton() {
        driver.findElement(Selectors.removeKeyButton).click();
        return this;
    }

    public SigningFragment clickVerifySignatureButton() {
        driver.findElement(Selectors.verifySignatureButton).click();
        return this;
    }

    public SigningFragment clickSignButton() {
        driver.findElement(Selectors.signButton).click();
        return this;
    }

    public SigningFragment createKey() {
        return clickCreateKeyButton().assertStatus(is(equalTo("Signing key created successfully")));
    }

    public String getSignature() {
        return driver.findElement(Selectors.signature).getText();
    }

    public String getPublicKey() {
        return driver.findElement(Selectors.publicKey).getText();
    }

    public boolean isKeyAvailable() {
        return isDisplayed(driver, Selectors.removeKeyButton);
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
        setText(driver.findElement(Selectors.signature), text);
        return this;
    }

    public SigningFragment setInput(CharSequence... text) {
        setText(driver.findElement(Selectors.input), text);
        return this;
    }

    public SigningFragment setPublicKey(CharSequence... text) {
        setText(driver.findElement(Selectors.publicKey), text);
        return this;
    }

    public SigningFragment waitUntilDisplayed() {
        assertText(driver, Selectors.title, is(equalTo("Sign")));
        return this;
    }

    private BiometricPromptFragment getBiometricPromptFragment() {
        if (null == biometricPromptFragment) {
            biometricPromptFragment = new BiometricPromptFragment(driver);
        }
        return biometricPromptFragment;
    }
}
