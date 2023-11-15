package com.labnoratory.android_device.e2e;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

import static com.labnoratory.android_device.e2e.E2EHelper.PACKAGE_NAME;
import static com.labnoratory.android_device.e2e.E2EHelper.adbShell;
import static com.labnoratory.android_device.e2e.E2EHelper.emulateBackButton;
import static com.labnoratory.android_device.e2e.E2EHelper.scanEnrolledFinger;
import static com.labnoratory.android_device.e2e.E2EHelper.sleep;
import static com.labnoratory.android_device.e2e.FragmentHelper.byText;
import static com.labnoratory.android_device.e2e.FragmentHelper.setText;

public class SecuritySettingsFragment {

    private static final By pinEntrySelector = By.id("com.android.settings:id/password_entry");
    private static final By deleteButtonSelector = By.id("com.android.settings:id/delete_button");
    private static final By deviceSecurityLabel = byText("DEVICE SECURITY");
    private static final By doneButtonSelector = byText("DONE");
    private static final By addFingerprintButtonSelector = byText("Add fingerprint");

    private final AndroidDriver driver;

    public static WebElement getAddFingerprintButton(WebDriver driver) {
        return driver.findElement(addFingerprintButtonSelector);
    }

    public static WebElement getDeleteButton(WebDriver driver) {
        return driver.findElement(deleteButtonSelector);
    }

    public static WebElement getDeleteConfirmButton(WebDriver driver) {
        List<WebElement> elements = driver.findElements(byText("Yes, remove"));
        if (elements.isEmpty()) elements = driver.findElements(byText("Delete"));
        return elements.get(0);
    }

    public static WebElement getDoneButton(WebDriver driver) {
        return driver.findElement(doneButtonSelector);
    }

    public static WebElement getFingerprintMenuItemElement(WebDriver driver) {
        return driver.findElement(byText("Fingerprint"));
    }

    public static WebElement getNextButton(WebDriver driver) {
        return driver.findElement(byText("NEXT"));
    }

    public static WebElement getPINInputElement(WebDriver driver) {
        return driver.findElement(pinEntrySelector);
    }

    public static void setupFingerprint(AndroidDriver driver) {
        String pin = "1111";
        adbShell("locksettings clear --old " + pin);
        sleep(500);
        adbShell("locksettings set-pin " + pin);
        sleep(500);
        SecuritySettingsFragment settingsFragment = new SecuritySettingsFragment(driver)
                .open()
                .clickFingerprintMenuItem()
                .enterPIN(pin);
        if (settingsFragment.hasFingersEnrolled()) {
            settingsFragment.removeFingers()
                    .clickAddFingerprintButton();
        } else {
            settingsFragment.clickNext();
        }
        settingsFragment
                .scanFingerprint()
                .clickDone();
        adbShell(String.format("am start %s/.MainActivity", PACKAGE_NAME));
        sleep(1000);
    }

    public SecuritySettingsFragment(AndroidDriver driver) {
        this.driver = driver;
    }

    /** @noinspection UnusedReturnValue*/
    public SecuritySettingsFragment clickAddFingerprintButton() {
        getAddFingerprintButton(driver).click();
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> webDriver.findElements(addFingerprintButtonSelector).isEmpty());
        return this;
    }

    public SecuritySettingsFragment clickFingerprintMenuItem() {
        getFingerprintMenuItemElement(driver).click();
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> !webDriver.findElements(byText("Re-enter your PIN")).isEmpty());
        return this;
    }

    public SecuritySettingsFragment enterPIN(CharSequence... text) {
        setText(getPINInputElement(driver), text);
        driver.pressKey(new KeyEvent(AndroidKey.ENTER));
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> webDriver.findElements(pinEntrySelector).isEmpty());
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SecuritySettingsFragment clickNext() {
        getNextButton(driver).click();
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> !webDriver.findElements(byText("Touch the sensor")).isEmpty());
        return this;
    }

    /** @noinspection UnusedReturnValue*/
    public SecuritySettingsFragment clickDone() {
        getDoneButton(driver).click();
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> webDriver.findElements(doneButtonSelector).isEmpty());
        return this;
    }

    public boolean hasFingersEnrolled() {
        return !driver.findElements(deleteButtonSelector).isEmpty();
    }

    public SecuritySettingsFragment open() {
        adbShell("am start -a android.settings.SECURITY_SETTINGS");
        int i = 0;
        while (driver.findElements(deviceSecurityLabel).isEmpty()) {
            emulateBackButton();
            sleep(500);
            if (i++ > 10) throw new RuntimeException("Failed to open security settings");
        }
        return this;
    }

    public SecuritySettingsFragment removeFingers() {
        while (hasFingersEnrolled()) {
            int initialDeleteButtonsCount = driver.findElements(deleteButtonSelector).size();
            getDeleteButton(driver).click();
            getDeleteConfirmButton(driver).click();
            new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(webDriver -> initialDeleteButtonsCount != driver.findElements(deleteButtonSelector).size());
        }
        return this;
    }

    public SecuritySettingsFragment scanFingerprint() {
        for (int i = 0; i < 3; i++) {
            scanEnrolledFinger();
            sleep(500);
        }
        new WebDriverWait(driver, Duration.ofSeconds(1))
                .until(webDriver -> !webDriver.findElements(doneButtonSelector).isEmpty());
        return this;
    }
}
