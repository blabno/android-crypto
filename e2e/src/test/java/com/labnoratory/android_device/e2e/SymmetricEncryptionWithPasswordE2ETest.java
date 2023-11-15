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

public class SymmetricEncryptionWithPasswordE2ETest {

    @BeforeMethod
    public void setUp() {
        new MainTabsFragment(AndroidDriverFactory.getInstance())
                .clickSymmetricEncryptionWithPassword()
                .setInput("")
                .setPassword("")
                .setSalt("")
                .setIterations("")
                .setCipherText("")
                .setIV("");
    }

    @Test
    public void encryptSymmetricallyWithPassword() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        String failedToDecryptMessage = "Failed to decrypt with password";
        String dataEncryptedSuccessfully = "Data encrypted successfully";
        Faker faker = Faker.instance();
        LinkedList<String> inputs = getUnique(2, () -> faker.chuckNorris().fact());
        String input1 = inputs.pop();
        String input2 = inputs.pop();
        String password = faker.beer().name();
        String salt = faker.beer().malt();
        int rawIterations = faker.number().numberBetween(1, 1000);
        String iterations = "" + rawIterations;
        String wrongPassword = password + "aa";
        String wrongSalt = salt + "aa";
        String wrongIterations = (rawIterations + 1) + "";

        SymmetricEncryptionWithPasswordFragment encryptionTab = new SymmetricEncryptionWithPasswordFragment(driver)
                .setInput(input1)
                .setPassword(password)
                .setSalt(salt)
                .setIterations(iterations)
                .clickEncryptButton()
                .assertStatus(is(equalTo(dataEncryptedSuccessfully)))
                .setInput("")
                .assertCipherText(is(not(emptyString())))
                .assertIV(is(not(emptyString())));
        String cipherText = encryptionTab.getCipherText();
        String iv = encryptionTab.getIV();
        encryptionTab
                .clickDecryptButton()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input1)))
                .setInput(input2)
                .clickEncryptButton()
                .assertStatus(is(equalTo(dataEncryptedSuccessfully)))
                .assertCipherText(is(not(equalTo(cipherText))))
                .assertIV(is(not(equalTo(iv))));

        encryptionTab.setPassword(wrongPassword)
                .clickDecryptButton()
                .assertStatus(is(equalTo(failedToDecryptMessage)));

        encryptionTab.setPassword(password)
                .setSalt(wrongSalt)
                .clickDecryptButton()
                .assertStatus(is(equalTo(failedToDecryptMessage)));

        encryptionTab.setSalt(salt)
                .setIterations(wrongIterations)
                .clickDecryptButton()
                .assertStatus(is(equalTo(failedToDecryptMessage)));

        encryptionTab
                .setIterations(iterations)
                .clickDecryptButton()
                .assertStatus(matchesPattern(String.format("Decryption result:\n.*\nString: %s", input2)));
    }
}