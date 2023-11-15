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

public class AsymmetricEncryptionFragment {

    private static class Selectors {
        private static final By authenticationRequired = byId("authenticationRequired");
        private static final By cipherText = byId("cipherText");
        private static final By createKeyButton = byId("createKeyButton");
        private static final By decryptButton = byId("decryptButton");
        private static final By encryptButton = byId("encryptButton");
        private static final By input = byId("input");
        private static final By removeKeyButton = byId("removeKeyButton");
        private static final By status = byId("status");
        private static final By title = byId("title");
    }

    private final AndroidDriver driver;
    private BiometricPromptFragment biometricPromptFragment;

    public AsymmetricEncryptionFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public AsymmetricEncryptionFragment assertCipherText(Matcher<String> matcher) {
        assertText(driver, Selectors.cipherText, matcher);
        return this;
    }

    public AsymmetricEncryptionFragment assertStatus(Matcher<String> matcher) {
        assertText(driver, Selectors.status, matcher);
        return this;
    }

    public AsymmetricEncryptionFragment assureKeyDoesNotRequireAuthentication() {
        WebElement authenticationRequired = driver.findElement(Selectors.authenticationRequired);
        if (authenticationRequired.getText().matches(".*ON.*")) {
            authenticationRequired.click();
        }
        assertText(driver, Selectors.authenticationRequired, matchesPattern(".*OFF.*"));
        return this;
    }

    public AsymmetricEncryptionFragment assureKeyRequiresAuthentication() {
        WebElement authenticationRequired = driver.findElement(Selectors.authenticationRequired);
        if (authenticationRequired.getText().matches(".*OFF.*")) {
            authenticationRequired.click();
        }
        assertText(driver, Selectors.authenticationRequired, matchesPattern(".*ON.*"));
        return this;
    }

    public AsymmetricEncryptionFragment cancelBiometrics() {
        getBiometricPromptFragment()
                .waitUntilDisplayed()
                .cancel();
        return this;
    }

    public AsymmetricEncryptionFragment clickCreateKeyButton() {
        driver.findElement(Selectors.createKeyButton).click();
        return this;
    }

    public AsymmetricEncryptionFragment clickRemoveKeyButton() {
        driver.findElement(Selectors.removeKeyButton).click();
        return this;
    }

    public AsymmetricEncryptionFragment clickDecryptButton() {
        driver.findElement(Selectors.decryptButton).click();
        return this;
    }

    public AsymmetricEncryptionFragment clickEncryptButton() {
        driver.findElement(Selectors.encryptButton).click();
        return this;
    }

    public AsymmetricEncryptionFragment createKey() {
        return clickCreateKeyButton()
                .assertStatus(is(equalTo("Encryption key created successfully")));
    }

    public String getCipherText() {
        return driver.findElement(Selectors.cipherText).getText();
    }

    public boolean isKeyAvailable() {
        return isDisplayed(driver, Selectors.removeKeyButton);
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
        setText(driver.findElement(Selectors.cipherText), text);
        return this;
    }

    public AsymmetricEncryptionFragment setInput(CharSequence... text) {
        setText(driver.findElement(Selectors.input), text);
        return this;
    }

    public AsymmetricEncryptionFragment waitUntilDisplayed() {
        assertText(driver, Selectors.title, is(equalTo("Encrypt\nasymmetrically")));
        return this;
    }

    private BiometricPromptFragment getBiometricPromptFragment() {
        if (null == biometricPromptFragment) {
            biometricPromptFragment = new BiometricPromptFragment(driver);
        }
        return biometricPromptFragment;
    }
}
