package com.labnoratory.android_device.e2e;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.net.URL;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AndroidDeviceE2ETest {

    private static AndroidDriver driver;

    @BeforeClass
    public static void beforeClass() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setApp("./sample-app/build/outputs/apk/debug/sample-app-debug.apk");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
    }

    @AfterClass
    public static void afterClass() {
        if (null != driver) driver.quit();
    }

    @Test
    public void authenticate() {
        setupFingerprint();
        adbShell("am start com.labnoratory.sample_app/.MainActivity");
        sleep(1000);
        WebElement status = driver.findElement(AppiumBy.id("status"));
        assertTrue(status.isDisplayed());
        assertEquals("Hello World!", status.getText());
        WebElement button = driver.findElement(AppiumBy.id("authenticateButton"));
        assertTrue(button.isDisplayed());
        button.click();
        sleep(1000);
        scanFinger(2);
        sleep(1000);
        scanFinger(1);
        sleep(1000);
        assertEquals("Authentication successful", status.getText());
    }

    private static void setupFingerprint() {
        adbShell("locksettings clear --old 1111");
        sleep(500);
        adbShell("locksettings set-pin 1111");
        sleep(500);
        adbShell("am start -a android.settings.SECURITY_SETTINGS");
        sleep(500);
        adbShell("input tap 274 1600");
        sleep(500);
        adbShell("input text 1111");
        sleep(500);
        adbShell("input keyevent 66");
        sleep(500);
        adbShell("input tap 1100 2200");
        sleep(500);
        for (int i = 0; i < 3; i++) {
            scanFinger(1);
            sleep(500);
        }
        scanFinger(1);
        sleep(500);
        scanFinger(1);
        sleep(500);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void adb(String cmd) {
        try {
            Runtime.getRuntime().exec("adb " + cmd);
        } catch (IOException e) {
            throw new RuntimeException("Failed to emulate fingerprint scanning", e);
        }
    }

    private static void adbShell(String cmd) {
        adb("shell " + cmd);
    }

    private static void scanFinger(int fingerIndex) {
        adb("-e emu finger touch " + fingerIndex);
    }

    /**
     * @noinspection unused
     */
    private static void emulateBackButton() {
        adbShell("input keyevent 4");
    }
}