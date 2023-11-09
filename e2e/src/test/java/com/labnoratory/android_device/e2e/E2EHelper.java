package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;

import java.io.IOException;

import io.appium.java_client.android.AndroidDriver;

public class E2EHelper {

    protected static final String PACKAGE_NAME = "com.labnoratory.sample_app";

    public static By byText(String text) {
        return By.xpath(String.format("//*[@text=\"%s\"]", text));
    }

    public static void setupFingerprint(AndroidDriver driver) {
        String pin = "1111";
        adbShell("locksettings clear --old " + pin);
        sleep(500);
        adbShell("locksettings set-pin " + pin);
        sleep(500);
        SecuritySettingsFragment settingsFragment = new SecuritySettingsFragment(driver)
                .open()
                .clickFingerprintMenuItem()
                .enterPIN(pin);
        if(settingsFragment.hasFingersEnrolled()) {
            settingsFragment.removeFingers()
                    .clickAddFingerprintButton();
        } else {
                    settingsFragment.clickNext();
        }
        settingsFragment
                .scanFingerprint()
                .clickDone();
        adbShell(String.format("am start %s/.MainActivity", PACKAGE_NAME));
        sleep(1000);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void adb(String cmd) {
        try {
            Runtime.getRuntime().exec("adb " + cmd);
        } catch (IOException e) {
            throw new RuntimeException("Failed to emulate fingerprint scanning", e);
        }
    }

    public static void adbShell(String cmd) {
        adb("shell " + cmd);
    }

    public static void scanFinger(int fingerIndex) {
        adb("-e emu finger touch " + fingerIndex);
    }

    public static void scanEnrolledFinger() {
        scanFinger(1);
    }

    public static void scanUnknownFinger() {
        scanFinger(2);
    }

    /**
     * @noinspection unused
     */
    public static void emulateBackButton() {
        adbShell("input keyevent 4");
    }
}
