package com.labnoratory.android_device.e2e;

import org.junit.Test;

public class AuthenticationE2ETest extends AbstractE2ETest {

    @Test
    public void authenticate___authentication_succeeds() {
        new MainTabsFragment(driver).clickAuthentication();
        new AuthenticationFragment(driver)
                .waitUntilDisplayed()
                .clickClearButton()
                .clickAuthenticateButton()
                .scanUnknownFinger()
                .scanEnrolledFinger()
                .assertStatus("Authentication successful");
    }

    @Test
    public void authenticate___authentication_cancelled() {
        new MainTabsFragment(driver).clickAuthentication();
        new AuthenticationFragment(driver)
                .waitUntilDisplayed()
                .clickClearButton()
                .clickAuthenticateButton()
                .cancelBiometricAuthentication()
                .assertStatus("Authentication failed");
    }

}