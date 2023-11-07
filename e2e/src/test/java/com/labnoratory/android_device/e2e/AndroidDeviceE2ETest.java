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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class AndroidDeviceE2ETest {

    private static AndroidDriver driver;

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
        WebElement status = driver.findElement(AppiumBy.id("status"));
        assertTrue(status.isDisplayed());
        assertEquals("", status.getText());
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
    }

    private static void clearAppData() {
        adbShell("pm clear start com.labnoratory.sample_app");
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
        adbShell("am start com.labnoratory.sample_app/.MainActivity");
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