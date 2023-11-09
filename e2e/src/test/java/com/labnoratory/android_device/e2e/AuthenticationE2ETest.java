package com.labnoratory.android_device.e2e;

import org.junit.Test;

import static com.labnoratory.android_device.e2e.E2EHelper.scanEnrolledFinger;
import static com.labnoratory.android_device.e2e.E2EHelper.scanUnknownFinger;
import static com.labnoratory.android_device.e2e.E2EHelper.sleep;

public class AuthenticationE2ETest extends AbstractE2ETest {

    @Test
    public void authenticate() {
        new MainTabsFragment(driver).clickAuthentication();
        AuthenticationFragment authenticationTab = new AuthenticationFragment(driver)
                .assertStatus("")
                .clickAuthenticateButton();
        sleep(1000);
        scanUnknownFinger();
        sleep(1000);
        scanEnrolledFinger();
        sleep(1000);
        authenticationTab.assertStatus("Authentication successful");
    }

}