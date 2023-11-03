package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.Cipher;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoInitializeAsymmetricCipherForDecryptionTest extends AbstractAndroidCryptoTest {

    @Test
    public void initializeAsymmetricCipherForDecryption___key_is_for_signing___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeAsymmetricCipherForDecryption(alias));
    }

    @Test
    public void initializeAsymmetricCipherForDecryption___key_not_found___throws_exception() {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class,() -> crypto.initializeAsymmetricCipherForDecryption(alias));
    }

    @Test
    public void initializeAsymmetricCipherForDecryption___returns_cipher() throws Exception {
        String alias = faker.app().name();

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        Cipher encryption = crypto.initializeAsymmetricCipherForDecryption(alias);
        assertNotNull(encryption);
    }

}