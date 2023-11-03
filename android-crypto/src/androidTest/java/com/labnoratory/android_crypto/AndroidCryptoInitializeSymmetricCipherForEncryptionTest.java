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
public class AndroidCryptoInitializeSymmetricCipherForEncryptionTest extends AbstractAndroidCryptoTest {

    @Test
    public void initializeSymmetricCipherForEncryption___key_is_for_asymmetric_encryption___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeSymmetricCipherForEncryption(alias));
    }

    @Test
    public void initializeSymmetricCipherForEncryption___key_is_for_signing___throws_exception() throws Exception {
        String alias = faker.app().name();

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(WrongKeyTypeException.class, () -> crypto.initializeSymmetricCipherForEncryption(alias));
    }

    @Test
    public void initializeSymmetricCipherForEncryption___key_not_found___throws_exception() {
        String alias = faker.app().name();

        assertThrows(KeyNotFoundException.class, () -> crypto.initializeSymmetricCipherForEncryption(alias));
    }


    @Test
    public void initializeSymmetricCipherForEncryption___returns_cipher() throws Exception {
        String alias = faker.app().name();
        byte[] input = randomBytes(77777);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        Cipher encryption = crypto.initializeSymmetricCipherForEncryption(alias);
        assertNotNull(encryption);
        byte[] encryptedBytes = encryption.doFinal(input);
        byte[] iv = encryption.getIV();

        Cipher decryption = crypto.initializeSymmetricCipherForDecryption(alias, iv);
        byte[] decryptedBytes = decryption.doFinal(encryptedBytes);

        assertArrayEquals(input, decryptedBytes);
    }

}