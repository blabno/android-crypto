package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.E2EHelper.sleep;
import static com.labnoratory.android_device.e2e.FragmentHelper.byText;

public class BiometricPromptFragment {

    private static final By biometricAuthenticationLabel = byText("Biometric Authentication");

    private final AndroidDriver driver;

    public BiometricPromptFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public BiometricPromptFragment scanEnrolledFinger() {
        E2EHelper.scanEnrolledFinger();
        waitUntilDisappears();
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public BiometricPromptFragment scanUnknownFinger() {
        E2EHelper.scanUnknownFinger();
        sleep(1000);
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public BiometricPromptFragment cancel() {
        driver.findElement(byText("Cancel")).click();
        waitUntilDisappears();
        return this;
    }

    public BiometricPromptFragment waitUntilDisplayed() {
        FragmentHelper.waitUntilDisplayed(driver, biometricAuthenticationLabel);
        return this;
    }

    /**
     * @noinspection UnusedReturnValue
     */
    public BiometricPromptFragment waitUntilDisappears() {
        FragmentHelper.waitUntilDisappears(driver, biometricAuthenticationLabel);
        return this;
    }
}
