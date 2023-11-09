package com.labnoratory.android_device.e2e;

import org.junit.Before;
import org.junit.Test;

import static com.labnoratory.android_device.e2e.Random.randomString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.assertNotEquals;

public class SymmetricEncryptionE2ETest extends AbstractE2ETest {

    @Before
    public void setUp() {
        new MainTabsFragment(driver).clickSymmetricEncryption();
        SymmetricEncryptionFragment encryptionTab = new SymmetricEncryptionFragment(driver);
        if (encryptionTab.isKeyAvailable()) {
            encryptionTab.removeKey();
        }
        encryptionTab.setInput("")
                .setCipherText("")
                .setIv("");
    }

    @Test
    public void encryptSymmetrically() {
        SymmetricEncryptionFragment encryptionTab = new SymmetricEncryptionFragment(driver);
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
}