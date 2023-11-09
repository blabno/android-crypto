package com.labnoratory.android_device.e2e;

import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.E2EHelper.sleep;
import static com.labnoratory.android_device.e2e.FragmentHelper.assertText;

public class BiometricPromptFragment {

    private final AndroidDriver driver;

    public BiometricPromptFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    /** @noinspection UnusedReturnValue*/
    public BiometricPromptFragment scanEnrolledFinger() {
        assertText(driver, webDriver -> webDriver.findElement(AppiumBy.xpath("//*[@text=\"Biometric Authentication\"]")), "Biometric Authentication");
        E2EHelper.scanEnrolledFinger();
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> {
                    try {
                        return webDriver.findElements(AppiumBy.xpath("//*[@text=\"Biometric Authentication\"]")).isEmpty();
                    } catch (Exception ignore) {
                        return true;
                    }
                });
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public BiometricPromptFragment scanUnknownFinger() {
        E2EHelper.scanUnknownFinger();
        sleep(1000);
        return this;
    }
}
