package com.labnoratory.android_device.e2e;

import org.junit.Before;
import org.junit.Test;

import static com.labnoratory.android_device.e2e.Random.randomInt;
import static com.labnoratory.android_device.e2e.Random.randomString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.assertNotEquals;

public class SymmetricEncryptionWithPasswordE2ETest extends AbstractE2ETest {

    @Before
    public void setUp() {
        new MainTabsFragment(driver).clickSymmetricEncryptionWithPassword();
        SymmetricEncryptionWithPasswordFragment encryptionTab = new SymmetricEncryptionWithPasswordFragment(driver);

        encryptionTab.setInput("")
                .setPassword("")
                .setSalt("")
                .setIterations("")
                .setCipherText("")
                .setIv("");
    }

    @Test
    public void encryptSymmetricallyWithPassword() {
        String failedToDecryptMessage = "Failed to decrypt with password";
        String dataEncryptedSuccessfully = "Data encrypted successfully";
        SymmetricEncryptionWithPasswordFragment encryptionTab = new SymmetricEncryptionWithPasswordFragment(driver);
        String input = randomString();
        String password = randomString();
        String salt = randomString();
        int rawIterations = randomInt(1000);
        String iterations = "" + rawIterations;
        String wrongPassword = password + "aa";
        String wrongSalt = salt + "aa";
        String wrongIterations = (rawIterations + 1) + "";
        encryptionTab
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
        encryptionTab
                .setInput(input)
                .clickEncryptButton()
                .assertStatus(dataEncryptedSuccessfully);
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
}