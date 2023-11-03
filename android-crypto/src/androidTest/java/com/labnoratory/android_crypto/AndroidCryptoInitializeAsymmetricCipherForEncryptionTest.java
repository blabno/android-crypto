package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import javax.crypto.Cipher;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoInitializeAsymmetricCipherForEncryptionTest extends AbstractAndroidCryptoTest {

    @Test
    public void initializeAsymmetricCipherForEncryption___key_is_for_signing___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeAsymmetricCipherForEncryption(crypto.getPublicKey(alias)));
    }

    @Test
    public void initializeAsymmetricCipherForEncryption___key_not_found___throws_exception() {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class, () -> crypto.initializeAsymmetricCipherForEncryption(crypto.getPublicKey(alias)));
    }

    @Test
    public void initializeAsymmetricCipherForEncryption___returns_cipher() throws Exception {
        String alias = faker.app().name();
        byte[] input = randomBytes(190);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        Cipher encryption = crypto.initializeAsymmetricCipherForEncryption(crypto.getPublicKey(alias));
        assertNotNull(encryption);
        byte[] encryptedBytes = encryption.doFinal(input);

        Cipher decryption = crypto.initializeAsymmetricCipherForDecryption(alias);
        byte[] decryptedBytes = decryption.doFinal(encryptedBytes);

        assertArrayEquals(input, decryptedBytes);
    }


}