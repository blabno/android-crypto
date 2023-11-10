package com.labnoratory.android_device.e2e;

import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;

public class AuthenticationE2ETest {

    @Test
    public void authenticate___authentication_succeeds() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
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
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new MainTabsFragment(driver).clickAuthentication();
        new AuthenticationFragment(driver)
                .waitUntilDisplayed()
                .clickClearButton()
                .clickAuthenticateButton()
                .cancelBiometricAuthentication()
                .assertStatus("Authentication failed");
    }

}