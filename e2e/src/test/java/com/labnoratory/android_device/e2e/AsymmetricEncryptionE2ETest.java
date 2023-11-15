package com.labnoratory.android_device.e2e;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.Random.randomString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.matchesPattern;

public class AsymmetricEncryptionE2ETest {

    @BeforeMethod
    public void setUp() {
        AsymmetricEncryptionFragment encryptionTab = new MainTabsFragment(AndroidDriverFactory.getInstance())
                .clickAsymmetricEncryption();
        if (encryptionTab.isKeyAvailable()) {
            encryptionTab.removeKey();
        }
        encryptionTab.setInput("")
                .setCipherText("");
    }

    @Test
    public void encryptAsymmetrically___key_does_not_require_authentication() {
        String input = randomString();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver)
                .assureKeyDoesNotRequireAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input)
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .setInput("")
                .assertCipherText(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        encryptionTab
                .clickDecryptButton()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input)))
                .setInput("Turbo")
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .assertCipherText(is(not(equalTo(cipherText))));

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .assertStatus(is(equalTo("Failed to decrypt with asymmetric key")));
    }

    @Test
    public void encryptAsymmetrically___key_requires_authentication() {
        String input = randomString();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input)
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .setInput("")
                .assertCipherText(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        encryptionTab.clickDecryptButton()
                .scanEnrolledFinger()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input)))
                .setInput("Turbo")
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .assertCipherText(is(not(equalTo(cipherText))));

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .scanEnrolledFinger()
                .assertStatus(is(equalTo("Failed to decrypt with asymmetric key")));
    }

    @Test
    public void encryptAsymmetrically___key_requires_authentication_but_user_cancels_authentication() {
        String input = randomString();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new AsymmetricEncryptionFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input)
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .clickDecryptButton()
                .cancelBiometrics()
                .assertStatus(is(equalTo("Failed to decrypt with asymmetric key")));
    }

}