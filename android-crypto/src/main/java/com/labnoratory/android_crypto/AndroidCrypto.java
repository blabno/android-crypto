package com.labnoratory.android_crypto;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;

public class AndroidCrypto {

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ALGORITHM = "RSA/ECB/OAEPPadding";
    private static final String RSA_KEY_ALGORITHM = "RSA";
    private static final String EC_KEY_ALGORITHM = "EC";
    private static final String KEY_STORE = "AndroidKeyStore";
    private static final String SECRET_KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    public AndroidCrypto() {
        if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
    }

    public boolean containsKey(@NonNull String alias) throws KeyStoreException {
        return getKeyStore().containsAlias(alias);
    }

    public PublicKey createAsymmetricEncryptionKey(@NonNull String alias, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, AliasConflictException {
        checkIfKeyExists(alias);
        KeyGenParameterSpec keyGenParameterSpec = getAsymmetricEncryptionKeyBuilder(alias, accessLevel, invalidateOnNewBiometry).build();
        String algorithm = KeyProperties.KEY_ALGORITHM_RSA;
        return generateKeyPair(keyGenParameterSpec, algorithm).getPublic();
    }

    public PublicKey createSigningKey(@NonNull String alias, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, AliasConflictException {
        checkIfKeyExists(alias);
        KeyGenParameterSpec keyGenParameterSpec = getSigningKeyBuilder(alias, accessLevel, invalidateOnNewBiometry).build();
        String algorithm = KeyProperties.KEY_ALGORITHM_EC;
        return generateKeyPair(keyGenParameterSpec, algorithm).getPublic();
    }

    public SecretKey createSymmetricEncryptionKey(@NonNull String alias, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, AliasConflictException {
        checkIfKeyExists(alias);
        KeyGenParameterSpec keyGenParameterSpec = getSymmetricEncryptionKeyBuilder(alias, accessLevel, invalidateOnNewBiometry).build();
        String algorithm = KeyProperties.KEY_ALGORITHM_AES;
        KeyGenerator generator = KeyGenerator.getInstance(algorithm, KEY_STORE);
        generator.init(keyGenParameterSpec);
        return generator.generateKey();
    }

    public void deleteKey(@NonNull String alias) throws KeyStoreException {
        getKeyStore().deleteEntry(alias);
    }

    public CompletableFuture<byte[]> decryptAsymmetrically(@NonNull String alias, @NonNull byte[] cipherText) {
        return decryptAsymmetrically(alias, cipherText, null);
    }

    public CompletableFuture<byte[]> decryptAsymmetrically(@NonNull String alias, @NonNull byte[] cipherText, @Nullable Authenticator authenticator) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        String exceptionMessage = "Failed to decrypt with asymmetric key";
        try {
            Cipher cipher = initializeAsymmetricCipherForDecryption(alias);

            KeyInfo keyInfo = getKeyInfo(alias);
            if (!keyInfo.isUserAuthenticationRequired()) {
                byte[] result = cipher.doFinal(cipherText);
                future.complete(result);
                return future;
            } else if (null == authenticator) {
                throw keyUserNotAuthenticated();
            }

            BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);
            authenticator.authenticate(cryptoObject)
                    .whenCompleteAsync((result, throwable) -> {
                        if (null != throwable) {
                            future.completeExceptionally(wrapException(exceptionMessage, throwable));
                            return;
                        }
                        try {
                            Cipher blessedCipher = Objects.requireNonNull(result.getCipher());
                            byte[] decryptionResult = blessedCipher.doFinal(cipherText);
                            future.complete(decryptionResult);
                        } catch (Exception e) {
                            future.completeExceptionally(wrapException(exceptionMessage, e));
                        }
                    });
        } catch (KeyNotFoundException | WrongKeyTypeException | KeyUserNotAuthenticated ex) {
            future.completeExceptionally(ex);
        } catch (RuntimeException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                 IllegalBlockSizeException e) {
            future.completeExceptionally(wrapException(exceptionMessage, e));
        }
        return future;
    }

    public CompletableFuture<byte[]> decryptSymmetrically(@NonNull String alias, @NonNull byte[] cipherText, @NonNull byte[] iv) {
        return decryptSymmetrically(alias, cipherText, iv, null);
    }

    public CompletableFuture<byte[]> decryptSymmetrically(@NonNull String alias, @NonNull byte[] cipherText, @NonNull byte[] iv, @Nullable Authenticator authenticator) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        String exceptionMessage = "Failed to decrypt with symmetric key";

        try {
            Cipher cipher = initializeSymmetricCipherForDecryption(alias, iv);

            KeyInfo keyInfo = getKeyInfo(alias);
            if (!keyInfo.isUserAuthenticationRequired()) {
                byte[] result = cipher.doFinal(cipherText);
                future.complete(result);
                return future;
            } else if (null == authenticator) {
                throw keyUserNotAuthenticated();
            }

            BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);
            authenticator.authenticate(cryptoObject)
                    .whenCompleteAsync((result, throwable) -> {
                        if (null != throwable) {
                            future.completeExceptionally(wrapException(exceptionMessage, throwable));
                            return;
                        }
                        try {
                            Cipher blessedCipher = Objects.requireNonNull(result.getCipher());
                            byte[] decryptionResult = blessedCipher.doFinal(cipherText);
                            future.complete(decryptionResult);
                        } catch (Exception e) {
                            future.completeExceptionally(wrapException(exceptionMessage, e));
                        }
                    });
        } catch (KeyNotFoundException | WrongKeyTypeException | KeyUserNotAuthenticated ex) {
            future.completeExceptionally(ex);
        } catch (BadPaddingException | IllegalBlockSizeException ex) {
            future.completeExceptionally(wrapException(exceptionMessage, ex));
        }
        return future;
    }

    public byte[] decryptSymmetricallyWithPassword(@NonNull byte[] password, @NonNull byte[] salt, int iterations, @NonNull byte[] cipherText, @NonNull byte[] iv) {
        SecretKey secretKey = deriveSecretKey(password, salt, iterations);
        try {
            Cipher cipher = initializeSymmetricCipherForDecryption(secretKey, iv);
            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt with password");
        }
    }

    public byte[] encryptAsymmetrically(@NonNull String alias, @NonNull byte[] bytesToEncrypt) throws KeyNotFoundException, WrongKeyTypeException {
        PublicKey publicKey = getPublicKey(alias);
        return encryptAsymmetrically(publicKey, bytesToEncrypt);
    }

    public byte[] encryptAsymmetrically(@NonNull PublicKey publicKey, @NonNull byte[] bytesToEncrypt) throws WrongKeyTypeException {
        try {
            Cipher cipher = initializeAsymmetricCipherForEncryption(publicKey);
            return doEncrypt(bytesToEncrypt, cipher).getCipherText();
        } catch (WrongKeyTypeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt with asymmetric key", e);
        }
    }

    public CompletableFuture<EncryptionResult> encryptSymmetrically(@NonNull String alias, @NonNull byte[] bytesToEncrypt) {
        return encryptSymmetrically(alias, bytesToEncrypt, null);
    }

    public CompletableFuture<EncryptionResult> encryptSymmetrically(@NonNull String alias, @NonNull byte[] bytesToEncrypt, @Nullable Authenticator authenticator) {
        CompletableFuture<EncryptionResult> future = new CompletableFuture<>();
        String exceptionMessage = "Failed to encrypt with symmetric key";
        try {
            Cipher cipher = initializeSymmetricCipherForEncryption(alias);

            KeyInfo keyInfo = getKeyInfo(alias);
            if (!keyInfo.isUserAuthenticationRequired()) {
                EncryptionResult result = doEncrypt(bytesToEncrypt, cipher);
                future.complete(result);
                return future;
            } else if (null == authenticator) {
                throw keyUserNotAuthenticated();
            }

            BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);
            authenticator.authenticate(cryptoObject)
                    .whenCompleteAsync((result, throwable) -> {
                        if (null != throwable) {
                            future.completeExceptionally(wrapException(exceptionMessage, throwable));
                            return;
                        }
                        try {
                            EncryptionResult encryptionResult = doEncrypt(bytesToEncrypt, Objects.requireNonNull(result.getCipher()));
                            future.complete(encryptionResult);
                        } catch (Exception e) {
                            future.completeExceptionally(wrapException(exceptionMessage, e));
                        }
                    });
        } catch (KeyNotFoundException | WrongKeyTypeException | KeyUserNotAuthenticated ex) {
            future.completeExceptionally(ex);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            future.completeExceptionally(wrapException(exceptionMessage, e));
        }
        return future;
    }

    public EncryptionResult encryptSymmetricallyWithPassword(@NonNull byte[] password, @NonNull byte[] salt, int iterations, @NonNull byte[] bytesToEncrypt)  {
        SecretKey secretKey = deriveSecretKey(password, salt, iterations);
        Cipher cipher = initializeSymmetricCipherForEncryption(secretKey);
        try {
            return doEncrypt(bytesToEncrypt, cipher);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Failed to encrypt with password", e);
        }
    }

    public SecretKey deriveSecretKey(@NonNull byte[] password, @NonNull byte[] salt, int iterations) {
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA256Digest());
        generator.init(password, salt, iterations);
        int AES_KEY_SIZE = 256;
        CipherParameters cipherParameters = generator.generateDerivedParameters(AES_KEY_SIZE);
        return new BCPBEKey(SECRET_KEY_DERIVATION_ALGORITHM, cipherParameters);
    }

    public KeyInfo getKeyInfo(@NonNull String alias) throws KeyNotFoundException {
        Key key = getKey(alias);
        try {
            if (key instanceof SecretKey) {
                return getKeyInfo((SecretKey) key);
            } else {
                KeyFactory factory = KeyFactory.getInstance(key.getAlgorithm(), KEY_STORE);
                return factory.getKeySpec(key, KeyInfo.class);
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to get key info for " + alias, e);
        }
    }

    public KeyInfo getKeyInfo(@NonNull SecretKey key) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(key.getAlgorithm(), KEY_STORE);
        return (KeyInfo) factory.getKeySpec(key, KeyInfo.class);
    }

    @NonNull
    public PublicKey getPublicKey(@NonNull String alias) throws KeyNotFoundException {
        try {
            Certificate certificate = getKeyStore().getCertificate(alias);
            if (null == certificate) {
                throw keyNotFound(alias);
            }
            return certificate.getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public Signature initializeSignatureForSigning(@NonNull String alias) throws NoSuchAlgorithmException, InvalidKeyException, KeyNotFoundException, WrongKeyTypeException {
        PrivateKey privateKey = getSigningPrivateKey(alias);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        return signature;
    }

    public Cipher initializeSymmetricCipherForEncryption(@NonNull SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize symmetric cipher for encryption");
        }
    }

    public Cipher initializeSymmetricCipherForEncryption(@NonNull String alias) throws KeyNotFoundException, WrongKeyTypeException {
        SecretKey secretKey = getSecretKey(alias);
        return initializeSymmetricCipherForEncryption(secretKey);
    }

    public Cipher initializeSymmetricCipherForDecryption(@NonNull String alias, @NonNull byte[] iv) throws KeyNotFoundException, WrongKeyTypeException {
        return initializeSymmetricCipherForDecryption(getSecretKey(alias), iv);
    }

    public Cipher initializeSymmetricCipherForDecryption(@NonNull SecretKey secretKey, @NonNull byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Failed to initialize symmetric cipher for decryption", e);
        }
    }

    public Cipher initializeAsymmetricCipherForEncryption(@NonNull PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, WrongKeyTypeException {
        checkKeyAlgorithm(publicKey, RSA_KEY_ALGORITHM);
        OAEPParameterSpec sp = getOAEPParameterSpec();
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, sp);
        return cipher;
    }

    public Cipher initializeAsymmetricCipherForDecryption(@NonNull String alias) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, KeyNotFoundException, WrongKeyTypeException {
        PrivateKey privateKey = getAsymmetricEncryptionPrivateKey(alias);
        OAEPParameterSpec spec = getOAEPParameterSpec();
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey, spec);
        return cipher;
    }

    public byte[] sign(@NonNull byte[] payload, @NonNull Signature signature) throws SignatureException {
        signature.update(payload);
        return signature.sign();
    }

    public CompletableFuture<byte[]> sign(@NonNull String alias, @NonNull byte[] payload) {
        return sign(alias, payload, null);
    }

    public CompletableFuture<byte[]> sign(@NonNull String alias, @NonNull byte[] payload, @Nullable Authenticator authenticator) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        String exceptionMessage = "Failed to sign message";
        try {
            Signature signature = initializeSignatureForSigning(alias);

            KeyInfo keyInfo = getKeyInfo(alias);
            if (!keyInfo.isUserAuthenticationRequired()) {
                signature.update(payload);
                future.complete(signature.sign());
                return future;
            } else if (null == authenticator) {
                throw keyUserNotAuthenticated();
            }

            BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(signature);
            authenticator.authenticate(cryptoObject)
                    .whenCompleteAsync((result, throwable) -> {
                        if (null != throwable) {
                            future.completeExceptionally(wrapException(exceptionMessage, throwable));
                            return;
                        }
                        try {
                            signature.update(payload);
                            future.complete(signature.sign());
                        } catch (Exception e) {
                            future.completeExceptionally(wrapException(exceptionMessage, e));
                        }
                    });
        } catch (KeyNotFoundException | WrongKeyTypeException | KeyUserNotAuthenticated ex) {
            future.completeExceptionally(ex);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            future.completeExceptionally(wrapException(exceptionMessage, e));
        }
        return future;
    }

    public boolean verifySignature(@NonNull String alias, @NonNull byte[] content, @NonNull byte[] signature) throws KeyNotFoundException, WrongKeyTypeException {
        PublicKey publicKey = getPublicKey(alias);
        return verifySignature(publicKey, content, signature);
    }

    public boolean verifySignature(@NonNull PublicKey publicKey, @NonNull byte[] content, @NonNull byte[] signature) throws WrongKeyTypeException {
        checkKeyAlgorithm(publicKey, EC_KEY_ALGORITHM);
        try {
            Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifier.initVerify(publicKey);
            verifier.update(content);
            return verifier.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify message", e);
        }
    }

    protected static KeyPair generateKeyPair(KeyGenParameterSpec keyGenParameterSpec, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, KEY_STORE);
        generator.initialize(keyGenParameterSpec);
        return generator.generateKeyPair();
    }

    protected static KeyGenParameterSpec.Builder getAsymmetricEncryptionKeyBuilder(@NonNull String alias, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
        builder
                .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setRandomizedEncryptionRequired(true);
        return applyOptions(builder, accessLevel, invalidateOnNewBiometry);
    }

    protected static KeyGenParameterSpec.Builder getSigningKeyBuilder(@NonNull String alias, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setRandomizedEncryptionRequired(true);
        return applyOptions(builder, accessLevel, invalidateOnNewBiometry);
    }

    protected static KeyGenParameterSpec.Builder getSymmetricEncryptionKeyBuilder(@NonNull String alias, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) {
        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
        builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true);
        return applyOptions(builder, accessLevel, invalidateOnNewBiometry);
    }

    protected static KeyGenParameterSpec.Builder applyOptions(@NonNull KeyGenParameterSpec.Builder builder, @NonNull AccessLevel accessLevel, boolean invalidateOnNewBiometry) {
        switch (accessLevel) {
            case UNLOCKED_DEVICE:
                builder.setUserAuthenticationRequired(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    builder.setUnlockedDeviceRequired(true);
                }
                break;
            case AUTHENTICATION_REQUIRED:
                // Sets whether this key is authorized to be used only if the user has been authenticated.
                builder.setUserAuthenticationRequired(true);
                // Allow pin/pass as a fallback on API 30+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_DEVICE_CREDENTIAL | KeyProperties.AUTH_BIOMETRIC_STRONG);
                }
                // Invalidate the keys if the user has registered a new biometric
                // credential. The variable "invalidatedByBiometricEnrollment" is true by default.
                builder.setInvalidatedByBiometricEnrollment(invalidateOnNewBiometry);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    builder.setIsStrongBoxBacked(true);
                }
                break;
        }
        return builder;
    }

    protected static KeyStore getKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException | CertificateException | IOException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get key store", e);
        }
    }

    @NonNull
    protected static OAEPParameterSpec getOAEPParameterSpec() {
        return new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
    }

    protected static Key getKey(String alias) throws KeyNotFoundException {
        try {
            Key key = getKeyStore().getKey(alias, null);
            if (null == key) {
                throw keyNotFound(alias);
            }
            return key;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException("Failed to get key from keystore", e);
        }
    }

    protected static PrivateKey getAsymmetricEncryptionPrivateKey(String alias) throws KeyNotFoundException, WrongKeyTypeException {
        Key key = getKey(alias);
        checkKeyAlgorithm(key, RSA_KEY_ALGORITHM);
        return (PrivateKey) key;
    }

    protected static PrivateKey getSigningPrivateKey(String alias) throws KeyNotFoundException, WrongKeyTypeException {
        Key key = getKey(alias);
        checkKeyAlgorithm(key, EC_KEY_ALGORITHM);
        return (PrivateKey) key;
    }

    protected static SecretKey getSecretKey(String alias) throws KeyNotFoundException, WrongKeyTypeException {
        Key key = getKey(alias);
        if (!(key instanceof SecretKey)) {
            throw new WrongKeyTypeException(String.format("Key %s is not a SecretKey", alias));
        }
        return (SecretKey) key;
    }

    private static void checkIfKeyExists(String alias) throws AliasConflictException {
        try {
            Key key = getKeyStore().getKey(alias, null);
            if (null != key) {
                throw new AliasConflictException();
            }
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to check if ke exists in the key store", e);
        } catch (UnrecoverableKeyException ignore) {
        }
    }

    private static void checkKeyAlgorithm(Key key, String expectedAlgorithm) throws WrongKeyTypeException {
        String actualAlgorithm = key.getAlgorithm();
        if (!expectedAlgorithm.equals(actualAlgorithm)) {
            throw new WrongKeyTypeException(String.format("Expected key algorithm %s, actual %s", expectedAlgorithm, actualAlgorithm));
        }
    }

    private static EncryptionResult doEncrypt(@NonNull byte[] bytesToEncrypt, @NonNull Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedBytes = cipher.doFinal(bytesToEncrypt);
        byte[] iv = cipher.getIV();
        return new EncryptionResult(encryptedBytes, iv);
    }

    private static KeyNotFoundException keyNotFound(String alias) {
        return new KeyNotFoundException("Key not found: " + alias);
    }

    private static KeyUserNotAuthenticated keyUserNotAuthenticated() {
        return new KeyUserNotAuthenticated();
    }

    private static RuntimeException wrapException(String message, Throwable cause) {
        return new RuntimeException(message, cause);
    }

    public enum AccessLevel {
        ALWAYS,
        UNLOCKED_DEVICE,
        AUTHENTICATION_REQUIRED;

        public static AccessLevel fromInt(int accessLevel) {
            switch (accessLevel) {
                case 0:
                    return ALWAYS;
                case 1:
                    return UNLOCKED_DEVICE;
                case 2:
                    return AUTHENTICATION_REQUIRED;
            }
            throw new IllegalArgumentException("Invalid access level");
        }
    }
}
