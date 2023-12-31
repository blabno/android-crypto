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

public class SymmetricEncryptionFragment {

    private static class Selectors {
        private static final By authenticationRequired = byId("authenticationRequired");
        private static final By cipherText = byId("cipherText");
        private static final By createKeyButton = byId("createKeyButton");
        private static final By decryptButton = byId("decryptButton");
        private static final By encryptButton = byId("encryptButton");
        private static final By input = byId("input");
        private static final By iv = byId("iv");
        private static final By removeKeyButton = byId("removeKeyButton");
        private static final By status = byId("status");
        private static final By title = byId("title");
    }

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public SymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public SymmetricEncryptionFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, Selectors.status, matcher);
        return this;
    }

    public SymmetricEncryptionFragment assertBiometricPromptDisplayed() {
        getBiometricPromptFragment().waitUntilDisplayed();
        return this;
    }

    public SymmetricEncryptionFragment assertCipherText(Matcher<String> matcher) {
        assertText(driver, Selectors.cipherText, matcher);
        return this;
    }

    public SymmetricEncryptionFragment assertIV(Matcher<String> matcher) {
        assertText(driver, Selectors.iv, matcher);
        return this;
    }

    public SymmetricEncryptionFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = driver.findElement(Selectors.authenticationRequired);
        Matcher<String> matcher = matchesPattern(".*ON.*");
        if (matcher.matches(authenticationRequired.getText())) {
            authenticationRequired.click();
        }
        assertText(driver, Selectors.authenticationRequired, not(matcher));
        return this;
    }

    public SymmetricEncryptionFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = driver.findElement(Selectors.authenticationRequired);
        Matcher<String> matcher = matchesPattern(".*ON.*");
        if (not(matcher).matches(authenticationRequired.getText())) {
            authenticationRequired.click();
        }
        assertText(driver, Selectors.authenticationRequired, matcher);
        return this;
    }

    public SymmetricEncryptionFragment cancelBiometrics() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .cancel();
        return this;
    }

    public SymmetricEncryptionFragment clickCreateKeyButton() {
        driver.findElement(Selectors.createKeyButton).click();
        return this;
    }

    public SymmetricEncryptionFragment clickRemoveKeyButton() {
        driver.findElement(Selectors.removeKeyButton).click();
        return this;
    }

    public SymmetricEncryptionFragment clickDecryptButton() {
        driver.findElement(Selectors.decryptButton).click();
        return this;
    }

    public SymmetricEncryptionFragment clickEncryptButton() {
        driver.findElement(Selectors.encryptButton).click();
        return this;
    }

    public SymmetricEncryptionFragment createKey() {
        return clickCreateKeyButton().assertStatus(is(equalTo("Encryption key created successfully")));
    }

    public String getCipherText() {
        return driver.findElement(Selectors.cipherText).getText();
    }

    public String getIv() {
        return driver.findElement(Selectors.iv).getText();
    }

    public boolean isKeyAvailable() {
        return isDisplayed(driver, Selectors.removeKeyButton);
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
        setText(driver.findElement(Selectors.cipherText), text);
        return this;
    }

    public SymmetricEncryptionFragment setInput(CharSequence... text) {
        setText(driver.findElement(Selectors.input), text);
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public SymmetricEncryptionFragment setIV(CharSequence... text) {
        setText(driver.findElement(Selectors.iv), text);
        return this;
    }

    public SymmetricEncryptionFragment waitUntilDisplayed() {
        assertText(driver, Selectors.title, is(equalTo("Encrypt\nsymmetrically")));
        return this;
    }

    private BiometricPromptFragment getBiometricPromptFragment() {
        if (null == biometricPromptFragment) {
            biometricPromptFragment = new BiometricPromptFragment(driver);
        }
        return biometricPromptFragment;
    }
}
