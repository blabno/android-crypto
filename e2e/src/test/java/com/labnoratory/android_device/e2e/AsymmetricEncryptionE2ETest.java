package com.labnoratory.android_device.e2e;

import org.junit.Test;

import static com.labnoratory.android_device.e2e.E2EHelper.clearAppData;
import static com.labnoratory.android_device.e2e.Random.randomString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.junit.Assert.assertNotEquals;

public class AsymmetricEncryptionE2ETest extends AbstractE2ETest {

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

}