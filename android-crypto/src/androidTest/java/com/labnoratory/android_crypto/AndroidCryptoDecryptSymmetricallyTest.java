package com.labnoratory.android_crypto;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.crypto.Cipher;

import androidx.biometric.BiometricPrompt;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.E2EUtil.failedFuture;
import static com.labnoratory.android_crypto.Random.getUnique;
import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoDecryptSymmetricallyTest extends AbstractAndroidCryptoTest {

    private static final String VALID_CIPHER_TEXT = "2f34e3243a816d827616ad8b888260971d8362d76ed44dada99f2eeeb38bf52535c24958f31d1a9d55d4428f723d2795c275e6dbb11682809ded437752b8c4e9e0f539dd1f092bd89ec92af9f5ab0172bd9624110d9bc361f8eb1ed6859f39b472d9e889c66771d8af43bd174a59fe21dbc5aac9e478e242fba14d423034d75169e35cff821eb86ec65c6df117df7244aa30090efc919ee9df06e624fde28aad1e31b91316e2b20c5cd1c26ec00190283dca6f27e3ee55495a22a301b24e3c9491d95c29e3238606459a4e8c02b6";
    private static final String VALID_IV = "458b873e89840b270f057915";

    @Test
    public void decryptSymmetrically___key_is_for_signing___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        byte[] iv = Hex.decode(VALID_IV);

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias, cipherText, iv).join());
        assertThat(exception.getCause(), is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void decryptSymmetrically___key_is_asymmetric___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        byte[] iv = Hex.decode(VALID_IV);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias, cipherText, iv).join());
        assertThat(exception.getCause(), is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void decryptSymmetrically___key_not_found___throws_exception() {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        byte[] iv = Hex.decode(VALID_IV);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias, cipherText, iv).join());
        assertThat(exception.getCause(), is(instanceOf(KeyNotFoundException.class)));
    }

    @Test
    public void decryptSymmetrically___encrypted_with_same_key___returns_decrypted_bytes() throws Exception {
        String alias = faker.app().name();

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        byte[] bytesToEncrypt = randomBytes(190);
        EncryptionResult encryptionResult = crypto.encryptSymmetrically(alias, bytesToEncrypt).join();
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();
        byte[] decryptedBytes = crypto.decryptSymmetrically(alias, cipherText, iv).join();
        assertArrayEquals(bytesToEncrypt, decryptedBytes);
    }

    @Test
    public void decryptSymmetrically___encrypted_with_different_key___throws_exception() throws Exception {
        LinkedList<String> aliases = getUnique(2, () -> faker.app().name());
        String alias1 = aliases.pop();
        String alias2 = aliases.pop();

        crypto.createSymmetricEncryptionKey(alias1, AndroidCrypto.AccessLevel.ALWAYS, false);
        crypto.createSymmetricEncryptionKey(alias2, AndroidCrypto.AccessLevel.ALWAYS, false);
        byte[] bytesToEncrypt = randomBytes(190);
        EncryptionResult encryptionResult = crypto.encryptSymmetrically(alias1, bytesToEncrypt).join();
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();
        RuntimeException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias2, cipherText, iv).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        assertNotNull(cause.getCause());
    }

    @Test
    public void decryptSymmetrically___key_requires_authentication_but_no_authenticator_passed___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        byte[] iv = Hex.decode(VALID_IV);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias, cipherText, iv).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertThat(cause, is(instanceOf(KeyUserNotAuthenticated.class)));
        assertEquals("Key requires user authentication but no authenticator passed", cause.getMessage());
    }

    @Test
    public void decryptSymmetrically___key_requires_authentication_and_authenticator_rejects___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        byte[] iv = Hex.decode(VALID_IV);
        RuntimeException rootCause = new RuntimeException("Random exception");
        Authenticator authenticator = mock(Authenticator.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(failedFuture(rootCause));

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias, cipherText, iv, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertEquals(rootCause, causeCause);
    }

    @Test
    public void decryptSymmetrically___key_requires_authentication_and_cipher_throws_during_doFinal___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] cipherText = Hex.decode(VALID_CIPHER_TEXT);
        byte[] iv = Hex.decode(VALID_IV);
        Authenticator authenticator = mock(Authenticator.class);
        Cipher cipher = mock(Cipher.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(CompletableFuture.completedFuture(new BiometricPrompt.CryptoObject(cipher)));

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.decryptSymmetrically(alias, cipherText, iv, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertThat(causeCause, is(instanceOf(IllegalStateException.class)));
    }

    private static void assertFailedToDecryptException(Throwable ex) {
        assertThat(ex, is(instanceOf(RuntimeException.class)));
        assertEquals("Failed to decrypt with symmetric key", ex.getMessage());
    }

}