package com.labnoratory.android_device.e2e;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.assertNotEquals;

public class AndroidDeviceE2ETest {

    private static AndroidDriver driver;

    private static final String PACKAGE_NAME = "com.labnoratory.sample_app";

    @BeforeClass
    public static void beforeClass() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setApp("./sample-app/build/outputs/apk/debug/sample-app-debug.apk");
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
        setupFingerprint();
    }

    @AfterClass
    public static void afterClass() {
        if (null != driver) driver.quit();
    }

    @Test
    public void authenticate() {
        new MainTabsFragment(driver).clickAuthentication();
        AuthenticationFragment authenticationTab = new AuthenticationFragment(driver)
                .assertStatus("")
                .clickAuthenticateButton();
        sleep(1000);
        scanFinger(2);
        sleep(1000);
        scanFinger(1);
        sleep(1000);
        authenticationTab.assertStatus("Authentication successful");
    }

    @Test
    public void encryptSymmetrically() {
        clearAppData();
        new MainTabsFragment(driver).clickSymmetricEncryption();
        SymmetricEncryptionFragment encryptionTab = new SymmetricEncryptionFragment(driver);
        assertThat(encryptionTab.getCipherText(), is(emptyString()));
        assertThat(encryptionTab.getIv(), is(emptyString()));
        encryptionTab
                .assertStatus("")
                .createKey()
                .assertStatus("Encryption key created successfully")
                .setInput("Bimbo")
                .clickEncryptButton()
                .assertStatus("Data encrypted successfully");
        String cipherText = encryptionTab.getCipherText();
        String iv = encryptionTab.getIv();
        assertThat(cipherText, is(not(emptyString())));
        assertThat(iv, is(not(emptyString())));
        encryptionTab.clickDecryptButton().assertStatus("Decryption result:\n.*\nString: Bimbo");
        encryptionTab.setInput("Turbo").clickEncryptButton().assertStatus("Data encrypted successfully");
        assertNotEquals(cipherText, encryptionTab.getCipherText());
        assertNotEquals(iv, encryptionTab.getIv());

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .assertStatus("Failed to decrypt with symmetric key");
    }

    private static void clearAppData() {
        adbShell("pm clear start " + PACKAGE_NAME);
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
        adbShell(String.format("am start %s/.MainActivity", PACKAGE_NAME));
        sleep(1000);
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