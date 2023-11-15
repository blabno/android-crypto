package com.labnoratory.android_device.e2e;

import com.github.javafaker.Faker;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;

import io.appium.java_client.android.AndroidDriver;

import static com.labnoratory.android_device.e2e.Random.getUnique;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyString;

public class SigningE2ETest {

    @BeforeMethod
    public void setUp() {
        SigningFragment signingTab = new MainTabsFragment(AndroidDriverFactory.getInstance())
                .clickSigning();
        if (signingTab.isKeyAvailable()) {
            signingTab.removeKey();
        }
        signingTab.setInput("")
                .setSignature("")
                .setPublicKey("");
    }

    @Test
    public void sign___key_does_not_require_authentication() {
        LinkedList<String> inputs = getUnique(3, () -> Faker.instance().ancient().hero());
        String input1 = inputs.pop();
        String input2 = inputs.pop();
        String input3 = inputs.pop();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        SigningFragment signingTab = new SigningFragment(driver)
                .assureKeyDoesNotRequireAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Signing key created successfully")))
                .setInput(input1)
                .clickSignButton()
                .assertStatus(is(equalTo("Data signed successfully")))
                .assertSignature(is(not(emptyString())))
                .assertPublicKey(is(not(emptyString())));
        String signature = signingTab.getSignature();
        String publicKey = signingTab.getPublicKey();
        signingTab
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature valid")))
                .setInput(input2)
                .clickSignButton()
                .assertStatus(is(equalTo("Data signed successfully")))
                .assertSignature(is(not(equalTo(signature))))
                .assertPublicKey(is(equalTo(publicKey)));

        signingTab.removeKey()
                .createKey()
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature valid")), "Although local key changed, provided public key should be valid for signature and input")
                .setPublicKey("")
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature invalid")), "Local key changed, so signature should be invalid")
                .clickSignButton()
                .assertStatus(is(equalTo("Data signed successfully")))
                .setInput(input3)
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature invalid")), "Input changed, so signature should be invalid")
                .assertPublicKey(is(not(equalTo(publicKey))));
    }

    @Test
    public void sign___key_requires_authentication() {
        LinkedList<String> inputs = getUnique(2, () -> Faker.instance().ancient().hero());
        String input1 = inputs.pop();
        String input2 = inputs.pop();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new SigningFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Signing key created successfully")))
                .setInput(input1)
                .clickSignButton()
                .assertBiometricPromptDisplayed()
                .scanEnrolledFinger()
                .assertStatus(is(equalTo("Data signed successfully")))
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature valid")))
                .setInput(input2)
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature invalid")));
    }

    @Test
    public void sign___key_requires_authentication_but_user_cancels_authentication() {
        String input = Faker.instance().ancient().hero();
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new SigningFragment(driver)
                .assureKeyRequiresAuthentication()
                .createKey()
                .assertStatus(is(equalTo("Signing key created successfully")))
                .setInput(input)
                .clickSignButton()
                .assertBiometricPromptDisplayed()
                .cancelBiometrics()
                .assertStatus(is(equalTo("Failed to sign message")))
                .assertSignature(is(emptyString()))
                .assertPublicKey(is(emptyString()));
    }

    @Test
    public void verifySignature___signature_valid() {
        AndroidDriver driver = AndroidDriverFactory.getInstance();
        new SigningFragment(driver)
                .setInput("Rabarbar")
                .setPublicKey("3059301306072a8648ce3d020106082a8648ce3d030107034200047a1a1bf678b98916f675712c680accdc60f9aa8b51565c2b2c0cd759fd94cc08a1343fa6806065a3db754d862a290d1b4895b5c287961e4cc0cec77c17279256")
                .setSignature("3045022100f85a7d8a98ed3b407f1a17070daef341878ff5cbc95c0f5091b7a5553531a7a10220099256da5cd8975186f97e4bb459986fce38e9821d62d179167bc15b3a7cb08a")
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature valid")))
                .setInput("Bueno")
                .clickVerifySignatureButton()
                .assertStatus(is(equalTo("Signature invalid")));
    }
}