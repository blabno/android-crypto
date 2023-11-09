package com.labnoratory.android_device.e2e;

import org.junit.Test;

public class AuthenticationE2ETest extends AbstractE2ETest {

    @Test
    public void authenticate() {
        new MainTabsFragment(driver).clickAuthentication();
        new AuthenticationFragment(driver)
                .assertStatus("")
                .clickAuthenticateButton()
                .scanUnknownFinger()
                .scanEnrolledFinger()
                .assertStatus("Authentication successful");
    }

}