package com.labnoratory.android_device.e2e;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import static com.labnoratory.android_device.e2e.Random.randomInt;
import static com.labnoratory.android_device.e2e.Random.randomString;
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
                .setApp("./sample-app/build/outputs/apk/debug/sample-app-debug.apk")
                ;
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
        String input = randomString();
        encryptionTab
                .assertStatus("")
                .createKey()
                .assertStatus("Encryption key created successfully")
                .setInput(input)
                .clickEncryptButton()
                .assertStatus("Data encrypted successfully")
                .setInput("");
        String cipherText = encryptionTab.getCipherText();
        String iv = encryptionTab.getIv();
        assertThat(cipherText, is(not(emptyString())));
        assertThat(iv, is(not(emptyString())));
        encryptionTab.clickDecryptButton().assertStatus(String.format("Decryption result:\n.*\nString: %s", input));
        encryptionTab.setInput("Turbo").clickEncryptButton().assertStatus("Data encrypted successfully");
        assertNotEquals(cipherText, encryptionTab.getCipherText());
        assertNotEquals(iv, encryptionTab.getIv());

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .assertStatus("Failed to decrypt with symmetric key");
    }

    @Test
    public void encryptWithPasswordSymmetrically() {
        String failedToDecryptMessage = "Failed to decrypt with password";
        String dataEncryptedSuccessfully = "Data encrypted successfully";
        clearAppData();
        new MainTabsFragment(driver).clickSymmetricEncryptionWithPassword();
        SymmetricEncryptionWithPasswordFragment encryptionTab = new SymmetricEncryptionWithPasswordFragment(driver);
        assertThat(encryptionTab.getCipherText(), is(emptyString()));
        assertThat(encryptionTab.getIv(), is(emptyString()));
        String input = randomString();
        String password = randomString();
        String salt = randomString();
        int rawIterations = randomInt(1000);
        String iterations = ""+ rawIterations;
        String wrongPassword = password+"aa";
        String wrongSalt = salt+"aa";
        String wrongIterations = (rawIterations+1)+"";
        encryptionTab
                .assertStatus("")
                .setInput(input)
                .setPassword(password)
                .setSalt(salt)
                .setIterations(iterations)
                .clickEncryptButton()
                .assertStatus(dataEncryptedSuccessfully)
                .setInput("");
        String cipherText = encryptionTab.getCipherText();
        String iv = encryptionTab.getIv();
        assertThat(cipherText, is(not(emptyString())));
        assertThat(iv, is(not(emptyString())));
        encryptionTab.clickDecryptButton().assertStatus(String.format("Decryption result:\n.*\nString: %s", input));
        input = "Turbo";
        encryptionTab.setInput(input).clickEncryptButton().assertStatus(dataEncryptedSuccessfully);
        assertNotEquals(cipherText, encryptionTab.getCipherText());
        assertNotEquals(iv, encryptionTab.getIv());

        encryptionTab.setPassword(wrongPassword)
                .clickDecryptButton()
                .assertStatus(failedToDecryptMessage);

        encryptionTab.setPassword(password)
                .setSalt(wrongSalt)
                .clickDecryptButton()
                .assertStatus(failedToDecryptMessage);

        encryptionTab.setSalt(salt)
                .setIterations(wrongIterations)
                .clickDecryptButton()
                .assertStatus(failedToDecryptMessage);

        encryptionTab
                .setIterations(iterations)
                .clickDecryptButton()
                .assertStatus(String.format("Decryption result:\n.*\nString: %s", input));
    }

    @Test
    public void encryptAsymmetrically() {
        clearAppData();
        new MainTabsFragment(driver).clickAsymmetricEncryption();
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver);
        assertThat(encryptionTab.getCipherText(), is(emptyString()));
        String input = randomString();
        encryptionTab
                .assertStatus("")
                .createKey()
                .assertStatus("Encryption key created successfully")
                .setInput(input)
                .clickEncryptButton()
                .assertStatus("Data encrypted successfully")
                .setInput("");
        String cipherText = encryptionTab.getCipherText();
        assertThat(cipherText, is(not(emptyString())));
        encryptionTab.clickDecryptButton().assertStatus(String.format("Decryption result:\n.*\nString: %s", input));
        encryptionTab.setInput("Turbo").clickEncryptButton().assertStatus("Data encrypted successfully");
        assertNotEquals(cipherText, encryptionTab.getCipherText());

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .assertStatus("Failed to decrypt with asymmetric key");
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