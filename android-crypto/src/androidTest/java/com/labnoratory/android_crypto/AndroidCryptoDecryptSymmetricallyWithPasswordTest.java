package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.randomBytes;
import static com.labnoratory.android_crypto.Random.randomInt;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoDecryptSymmetricallyWithPasswordTest extends AbstractAndroidCryptoTest {

    @Test
    public void decryptSymmetricallyWithPassword___invalid_password___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);
        byte[] password = randomBytes(100);
        byte[] salt = randomBytes(100);
        int iterations = randomInt(2000);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        EncryptionResult encryptionResult = crypto.encryptSymmetricallyWithPassword(password, salt, iterations, bytesToEncrypt);
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();

        password[0]++;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> crypto.decryptSymmetricallyWithPassword(password, salt, iterations, cipherText, iv));
        assertEquals("Failed to decrypt with password", exception.getMessage());
    }

    @Test
    public void decryptSymmetricallyWithPassword___invalid_salt___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);
        byte[] password = randomBytes(100);
        byte[] salt = randomBytes(100);
        int iterations = randomInt(2000);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        EncryptionResult encryptionResult = crypto.encryptSymmetricallyWithPassword(password, salt, iterations, bytesToEncrypt);
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();

        salt[0]++;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> crypto.decryptSymmetricallyWithPassword(password, salt, iterations, cipherText, iv));
        assertEquals("Failed to decrypt with password", exception.getMessage());
    }

    @Test
    public void decryptSymmetricallyWithPassword___invalid_iterations___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);
        byte[] password = randomBytes(100);
        byte[] salt = randomBytes(100);
        int iterations = randomInt(2000);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        EncryptionResult encryptionResult = crypto.encryptSymmetricallyWithPassword(password, salt, iterations, bytesToEncrypt);
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();

        int wrongIterations = iterations+1;

        RuntimeException exception = assertThrows(RuntimeException.class, () -> crypto.decryptSymmetricallyWithPassword(password, salt, wrongIterations, cipherText, iv));
        assertEquals("Failed to decrypt with password", exception.getMessage());
    }

    @Test
    public void decryptSymmetricallyWithPassword___valid_password_and_salt_and_iterations___returns_decrypted_bytes() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);
        byte[] password = randomBytes(100);
        byte[] salt = randomBytes(100);
        int iterations = randomInt(2000);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        EncryptionResult encryptionResult = crypto.encryptSymmetricallyWithPassword(password, salt, iterations, bytesToEncrypt);
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();

        byte[] decryptedBytes = crypto.decryptSymmetricallyWithPassword(password, salt, iterations, cipherText, iv);
        assertArrayEquals(bytesToEncrypt, decryptedBytes);
    }

}