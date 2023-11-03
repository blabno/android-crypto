package com.labnoratory.android_crypto;

import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PublicKey;
import java.util.LinkedList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static com.labnoratory.android_crypto.Random.getUnique;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoCreateAsymmetricEncryptionKeyTest extends AbstractAndroidCryptoTest {

    @Test
    public void createAsymmetricEncryptionKey___access_level_is_always___creates_properly_configured_key() throws Exception {
        String alias = faker.app().name();

        AndroidCrypto.AccessLevel accessLevel = AndroidCrypto.AccessLevel.ALWAYS;
        boolean invalidateOnNewBiometry = false;
        PublicKey publicKey = crypto.createAsymmetricEncryptionKey(alias, accessLevel, invalidateOnNewBiometry);
        assertNotNull(publicKey);

        KeyInfo keyInfo = crypto.getKeyInfo(alias);
        assertFalse(keyInfo.isInvalidatedByBiometricEnrollment());
        assertFalse(keyInfo.isTrustedUserPresenceRequired());
        assertFalse(keyInfo.isUserAuthenticationRequired());
        assertFalse(keyInfo.isUserAuthenticationRequirementEnforcedBySecureHardware());
        assertFalse(keyInfo.isUserAuthenticationValidWhileOnBody());
        assertFalse(keyInfo.isUserConfirmationRequired());

        assertEquals(KeyProperties.PURPOSE_DECRYPT + KeyProperties.PURPOSE_ENCRYPT, keyInfo.getPurposes());
    }

    @Test
    public void createAsymmetricEncryptionKey___access_level_is_unlocked_device___creates_properly_configured_key() throws Exception {
        String alias = faker.app().name();

        AndroidCrypto.AccessLevel accessLevel = AndroidCrypto.AccessLevel.UNLOCKED_DEVICE;
        boolean invalidateOnNewBiometry = false;
        PublicKey publicKey = crypto.createAsymmetricEncryptionKey(alias, accessLevel, invalidateOnNewBiometry);
        assertNotNull(publicKey);

        KeyInfo keyInfo = crypto.getKeyInfo(alias);
        assertFalse(keyInfo.isInvalidatedByBiometricEnrollment());
        assertFalse(keyInfo.isTrustedUserPresenceRequired());
        assertFalse(keyInfo.isUserAuthenticationRequired());
        assertFalse(keyInfo.isUserAuthenticationRequirementEnforcedBySecureHardware());
        assertFalse(keyInfo.isUserAuthenticationValidWhileOnBody());
        assertFalse(keyInfo.isUserConfirmationRequired());

        assertEquals(KeyProperties.PURPOSE_DECRYPT + KeyProperties.PURPOSE_ENCRYPT, keyInfo.getPurposes());
    }

    @Test
    public void createAsymmetricEncryptionKey___access_level_is_authentication_required___creates_properly_configured_key() throws Exception {
        String alias = faker.app().name();

        AndroidCrypto.AccessLevel accessLevel = AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED;
        boolean invalidateOnNewBiometry = false;
        PublicKey publicKey = crypto.createAsymmetricEncryptionKey(alias, accessLevel, invalidateOnNewBiometry);
        assertNotNull(publicKey);

        KeyInfo keyInfo = crypto.getKeyInfo(alias);
        assertFalse(keyInfo.isInvalidatedByBiometricEnrollment());
        assertFalse(keyInfo.isTrustedUserPresenceRequired());
        assertTrue(keyInfo.isUserAuthenticationRequired());
        assertFalse(keyInfo.isUserAuthenticationRequirementEnforcedBySecureHardware());
        assertFalse(keyInfo.isUserAuthenticationValidWhileOnBody());
        assertFalse(keyInfo.isUserConfirmationRequired());

        assertEquals(KeyProperties.PURPOSE_DECRYPT + KeyProperties.PURPOSE_ENCRYPT, keyInfo.getPurposes());
    }

    @Test
    public void createAsymmetricEncryptionKey___aliasAlreadyTaken() throws Exception {
        LinkedList<String> aliases = getUnique(3, () -> faker.app().name());
        String symmetricEncryptionKeyAlias = aliases.pop();
        String asymmetricEncryptionKeyAlias = aliases.pop();
        String signingKeyAlias = aliases.pop();

        crypto.createAsymmetricEncryptionKey(asymmetricEncryptionKeyAlias, AndroidCrypto.AccessLevel.ALWAYS, false);
        crypto.createSigningKey(signingKeyAlias, AndroidCrypto.AccessLevel.ALWAYS, false);
        crypto.createSymmetricEncryptionKey(symmetricEncryptionKeyAlias, AndroidCrypto.AccessLevel.ALWAYS, false);

        assertThrows(AliasConflictException.class, () -> crypto.createAsymmetricEncryptionKey(asymmetricEncryptionKeyAlias, AndroidCrypto.AccessLevel.ALWAYS, false));
    }

    @Test
    public void createRemoveAsymmetricEncryptionKey() throws Exception {
        createRemoveKeyScenario(alias -> {
            try {
                return crypto.createAsymmetricEncryptionKey(alias, AndroidCrypto.AccessLevel.ALWAYS, false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}