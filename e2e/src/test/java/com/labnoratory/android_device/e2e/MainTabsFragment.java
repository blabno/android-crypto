package com.labnoratory.android_device.e2e;

import org.openqa.selenium.WebElement;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.FragmentHelper.byText;

public class MainTabsFragment {

    private final AndroidDriver driver;

    public MainTabsFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    private static WebElement getTabs(AndroidDriver driver) {
        return driver.findElement(AppiumBy.id("tabLayout"));
    }

    public AsymmetricEncryptionFragment clickAsymmetricEncryption() {
        getTabs(driver).findElement(byText("Encrypt\nasymmetrically")).click();
        return new AsymmetricEncryptionFragment(driver).waitUntilDisplayed();
    }

    public SymmetricEncryptionFragment clickSymmetricEncryption() {
        getTabs(driver).findElement(byText("Encrypt\nsymmetrically")).click();
        return new SymmetricEncryptionFragment(driver).waitUntilDisplayed();
    }

    public SymmetricEncryptionWithPasswordFragment clickSymmetricEncryptionWithPassword() {
        getTabs(driver).findElement(byText("Encrypt\nwith password")).click();
        return new SymmetricEncryptionWithPasswordFragment(driver).waitUntilDisplayed();
    }

    public AuthenticationFragment clickAuthentication() {
        getTabs(driver).findElement(byText("Authenticate")).click();
        return new AuthenticationFragment(driver).waitUntilDisplayed();
    }

    public SigningFragment clickSigning() {
        getTabs(driver).findElement(byText("Sign")).click();
        return new SigningFragment(driver).waitUntilDisplayed();
    }
}
