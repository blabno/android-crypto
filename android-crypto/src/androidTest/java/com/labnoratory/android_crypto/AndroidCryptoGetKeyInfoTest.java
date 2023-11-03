package com.labnoratory.android_crypto;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

@RunWith(AndroidJUnit4.class)
public class AndroidCryptoGetKeyInfoTest extends AbstractAndroidCryptoTest {

    @Test
    public void getKeyInfo___key_not_found___throws_exception() throws Exception {
        String alias = faker.app().name();
        assertFalse(crypto.containsKey(alias));
        assertThrows(KeyNotFoundException.class, () -> crypto.getKeyInfo(alias));
    }

}