package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.crypto.Cipher;

import androidx.biometric.BiometricPrompt;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.E2EUtil.failedFuture;
import static com.labnoratory.android_crypto.Random.randomBytes;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoEncryptSymmetricallyTest extends AbstractAndroidCryptoTest {

    @Test
    public void encryptSymmetrically___key_is_for_signing___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        crypto.createSigningKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.encryptSymmetrically(alias, bytesToEncrypt).join());
        assertThat(exception.getCause(), is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void encryptSymmetrically___key_is_asymmetric___throws_exception() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.encryptSymmetrically(alias, bytesToEncrypt).join());
        assertThat(exception.getCause(), is(instanceOf(WrongKeyTypeException.class)));
    }

    @Test
    public void encryptSymmetrically___key_not_found___throws_exception() {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.encryptSymmetrically(alias, bytesToEncrypt).join());
        assertThat(exception.getCause(), is(instanceOf(KeyNotFoundException.class)));
    }

    @Test
    public void encryptSymmetrically___returns_cipherText_and_iv() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
        EncryptionResult encryptionResult = crypto.encryptSymmetrically(alias, bytesToEncrypt).join();
        assertNotNull(encryptionResult);
        byte[] cipherText = encryptionResult.getCipherText();
        byte[] iv = encryptionResult.getInitializationVector();
        assertNotNull(cipherText);
        assertNotEquals(0, cipherText.length);
        assertNotNull(iv);
        assertNotEquals(0, iv.length);
    }

    @Test
    public void encryptSymmetrically___key_requires_authentication_but_no_authenticator_passed___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] bytesToEncrypt = randomBytes(7777);

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.encryptSymmetrically(alias, bytesToEncrypt).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertThat(cause, is(instanceOf(KeyUserNotAuthenticated.class)));
        assertEquals("Key requires user authentication but no authenticator passed", cause.getMessage());
    }

    @Test
    public void encryptSymmetrically___key_requires_authentication_and_authenticator_rejects___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] bytesTwoEncrypt = randomBytes(7777);
        RuntimeException rootCause = new RuntimeException("Random exception");
        Authenticator authenticator = mock(Authenticator.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(failedFuture(rootCause));

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.encryptSymmetrically(alias, bytesTwoEncrypt, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertEquals(rootCause, causeCause);
    }

    @Test
    public void encryptSymmetrically___key_requires_authentication_and_cipher_throws_during_doFinal___completes_exceptionally() throws Exception {
        String alias = faker.app().name();
        byte[] bytesTwoEncrypt = randomBytes(7777);
        Authenticator authenticator = mock(Authenticator.class);
        Cipher cipher = mock(Cipher.class);

        when(authenticator.authenticate(any(BiometricPrompt.CryptoObject.class))).thenReturn(CompletableFuture.completedFuture(new BiometricPrompt.CryptoObject(cipher)));

        crypto.createSymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED, false);

        CompletionException exception = assertThrows(CompletionException.class, () -> crypto.encryptSymmetrically(alias, bytesTwoEncrypt, authenticator).join());
        Throwable cause = exception.getCause();
        assertNotNull(cause);
        assertFailedToDecryptException(cause);
        Throwable causeCause = cause.getCause();
        assertThat(causeCause, is(instanceOf(IllegalStateException.class)));
    }

    private static void assertFailedToDecryptException(Throwable ex) {
        assertThat(ex, is(instanceOf(RuntimeException.class)));
        assertEquals("Failed to encrypt with symmetric key", ex.getMessage());
    }

}