package com.labnoratory.android_device.e2e;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.Random.randomString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

public class SymmetricEncryptionE2ETest {

    @BeforeMethod
    public void setUp() {
        SymmetricEncryptionFragment encryptionTab = new MainTabsFragment(AndroidDriverFactory.getInstance())
                .clickSymmetricEncryption();
        if (encryptionTab.isKeyAvailable()) {
            encryptionTab.removeKey();
        }
        encryptionTab.setInput("")
                .setCipherText("")
                .setIV("");
    }

    @Test
    public void encryptSymmetrically___key_does_not_require_authentication() {
        String input = randomString();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        SymmetricEncryptionFragment encryptionTab = new SymmetricEncryptionFragment(driver)
                .assureKeyDoesNotRequireAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input)
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .setInput("")
                .assertCipherText(is(not(emptyString())))
                .assertIV(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        String iv = encryptionTab.getIv();
        encryptionTab
                .clickDecryptButton()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input)))
                .setInput("Turbo")
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .assertCipherText(is(not(equalTo(cipherText))))
                .assertIV(is(not(equalTo(iv))));

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .assertStatus(is(equalTo("Failed to decrypt with symmetric key")));
    }

    @Test
    public void encryptSymmetrically___key_requires_authentication() {
        String input = randomString();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        SymmetricEncryptionFragment encryptionTab = new SymmetricEncryptionFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input)
                .clickEncryptButton()
                .assertBiometricPromptDisplayed()
                .scanEnrolledFinger()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .setInput("")
                .assertCipherText(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        encryptionTab.clickDecryptButton()
                .assertBiometricPromptDisplayed()
                .scanEnrolledFinger()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input)))
                .setInput("Turbo")
                .clickEncryptButton()
                .scanEnrolledFinger()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .assertCipherText(is(not(equalTo(cipherText))));

        encryptionTab.removeKey()
                .createKey()
                .clickDecryptButton()
                .scanEnrolledFinger()
                .assertStatus(is(equalTo("Failed to decrypt with symmetric key")));
    }

    @Test
    public void encryptSymmetrically___key_requires_authentication_but_user_cancels_authentication() {
        String input = randomString();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new SymmetricEncryptionFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input)
                .clickEncryptButton()
                .cancelBiometrics()
                .assertStatus(is(equalTo("Failed to encrypt with symmetric key")))
                .assertCipherText(is(emptyString()))
                .assertIV(is(emptyString()));
    }
}