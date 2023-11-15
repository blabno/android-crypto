package com.labnoratory.android_device.e2e;

import com.github.javafaker.Faker;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.Random.getUnique;
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
        LinkedList<String> inputs = getUnique(2, () -> Faker.instance().ancient().hero());
        String input1 = inputs.pop();
        String input2 = inputs.pop();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver)
                .assureKeyDoesNotRequireAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input1)
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .setInput("")
                .assertCipherText(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        encryptionTab
                .clickDecryptButton()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input1)))
                .setInput(input2)
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
        LinkedList<String> inputs = getUnique(2, () -> Faker.instance().ancient().hero());
        String input1 = inputs.pop();
        String input2 = inputs.pop();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        AsymmetricEncryptionFragment encryptionTab = new AsymmetricEncryptionFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Encryption key created successfully")))
                .setInput(input1)
                .clickEncryptButton()
                .assertStatus(is(equalTo("Data encrypted successfully")))
                .setInput("")
                .assertCipherText(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        encryptionTab.clickDecryptButton()
                .scanEnrolledFinger()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input1)))
                .setInput(input2)
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
        String input = Faker.instance().ancient().hero();
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