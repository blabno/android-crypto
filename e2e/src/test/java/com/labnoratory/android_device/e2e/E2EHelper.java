package com.labnoratory.android_device.e2e;

import java.io.IOException;

public class E2EHelper {

    protected static final String PACKAGE_NAME = "com.labnoratory.sample_app";

    public static void clearAppData() {
        adbShell("pm clear start " + PACKAGE_NAME);
    }

    public static void setupFingerprint() {
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
        scanEnrolledFinger();
        sleep(500);
        scanEnrolledFinger();
        sleep(500);
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
