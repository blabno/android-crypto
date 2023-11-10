package com.labnoratory.android_device.e2e;

import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.Random.randomString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.testng.Assert.assertNotEquals;

public class AsymmetricEncryptionE2ETest {

    public void setUp(AndroidDriver driver) {
        new MainTabsFragment(driver).clickAsymmetricEncryption();
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver);
        if (encryptionTab.isKeyAvailable()) {
            encryptionTab.removeKey();
        }
        encryptionTab.setInput("")
                .setCipherText("");
    }

    @Test
    public void encryptAsymmetrically___key_does_not_require_authentication() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        setUp(driver);
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver);
        String input = randomString();
        encryptionTab
                .assureKeyDoesNotRequireAuthentication()
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

    @Test
    public void encryptAsymmetrically___key_requires_authentication() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        setUp(driver);
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver);
        String input = randomString();
        encryptionTab
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus("Encryption key created successfully")
                .setInput(input)
                .clickEncryptButton()
                .assertStatus("Data encrypted successfully")
                .setInput("");
        String cipherText = encryptionTab.getCipherText();
        assertThat(cipherText, is(not(emptyString())));
        encryptionTab.clickDecryptButton()
                .scanEnrolledFinger()
                .assertStatus(String.format("Decryption result:\n.*\nString: %s", input))
                .setInput("Turbo").clickEncryptButton().assertStatus("Data encrypted successfully");
        assertNotEquals(cipherText, encryptionTab.getCipherText());

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .scanEnrolledFinger()
                .assertStatus("Failed to decrypt with asymmetric key");
    }

}