package com.labnoratory.android_device.e2e;

import org.openqa.selenium.WebElement;

import java.util.List;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class MainTabsFragment {

    private final AndroidDriver driver;

    public MainTabsFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    public List<WebElement> getTabs() {
        return driver.findElements(AppiumBy.xpath("//*[contains(@resource-id,\":id/tabLayout\")]//android.widget.TextView"));
    }

    public AsymmetricEncryptionFragment clickAsymmetricEncryption() {
        getTabs().get(2).click();
        return new AsymmetricEncryptionFragment(driver).waitUntilDisplayed();
    }

    public SymmetricEncryptionFragment clickSymmetricEncryption() {
        getTabs().get(1).click();
        return new SymmetricEncryptionFragment(driver).waitUntilDisplayed();
    }

    public SymmetricEncryptionWithPasswordFragment clickSymmetricEncryptionWithPassword() {
        getTabs().get(3).click();
        return new SymmetricEncryptionWithPasswordFragment(driver).waitUntilDisplayed();
    }

    public AuthenticationFragment clickAuthentication() {
        getTabs().get(0).click();
        return new AuthenticationFragment(driver).waitUntilDisplayed();
    }

    public SigningFragment clickSigning() {
        getTabs().get(4).click();
        return new SigningFragment(driver).waitUntilDisplayed();
    }
}
