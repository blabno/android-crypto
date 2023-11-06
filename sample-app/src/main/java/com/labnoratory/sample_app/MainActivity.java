package com.labnoratory.sample_app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.labnoratory.android_crypto.AndroidCrypto;
import com.labnoratory.android_crypto.Authenticator;
import com.labnoratory.android_crypto.EncryptionResult;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.util.concurrent.CompletableFuture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SYMMETRIC_ENCRYPTION_KEY = "symmetric-encryption";

    private final AndroidCrypto crypto = new AndroidCrypto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateVisibility();
        updateAuthenticationRequiredText();
        TextView status = findViewById(R.id.status);
        status.setOnLongClickListener(view -> {
            CharSequence text = ((TextView) view).getText();
            if (text.length() == 0) return false;
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("Status", text));
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                Toast.makeText(MainActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            status.setText("");
            return true;
        });
    }

    public void onAuthenticateClick(View view) {
        Authenticator.authenticate(this).whenComplete((cryptoObject, throwable) -> {
            if (null != throwable) {
                handleError(throwable);
                return;
            }
            setStatus("Authentication successful");
        });
    }

    public void onAuthenticationRequiredClick(View view) {
        updateAuthenticationRequiredText();
    }

    public void onCreateSymmetricKeyClick(View view) {
        try {
            boolean authenticationRequired = ((SwitchCompat) findViewById(R.id.authenticationRequired)).isChecked();
            AndroidCrypto.AccessLevel accessLevel = authenticationRequired ? AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED : AndroidCrypto.AccessLevel.ALWAYS;
            crypto.createSymmetricEncryptionKey(SYMMETRIC_ENCRYPTION_KEY, accessLevel, false);
            setStatus("Symmetric encryption key created successfully");
            updateVisibility();
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void onDecryptSymmetricallyClick(View view) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            byte[] cipherText = Hex.decodeHex(((TextView) findViewById(R.id.cipherText)).getText().toString());
            byte[] iv = Hex.decodeHex(((TextView) findViewById(R.id.iv)).getText().toString());
            if (!crypto.getKeyInfo(SYMMETRIC_ENCRYPTION_KEY).isUserAuthenticationRequired()) {
                try {
                    future.complete(crypto.decrypt(SYMMETRIC_ENCRYPTION_KEY, cipherText, iv));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                crypto.decrypt(this, SYMMETRIC_ENCRYPTION_KEY, cipherText, iv)
                        .whenComplete((encryptionResult, throwable) -> {
                            if (null != throwable) {
                                future.completeExceptionally(throwable);
                            } else future.complete(encryptionResult);
                        });
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        future.whenComplete((decryptionResult, throwable) -> {
            if (null != throwable) {
                handleError(throwable);
                return;
            }
            setStatus(String.format("Decryption result:\nHex: %s\nString: %s", Hex.encodeHexString(decryptionResult), new String(decryptionResult)));
        });
    }

    public void onEncryptSymmetricallyClick(View view) {
        CompletableFuture<EncryptionResult> future = new CompletableFuture<>();
        try {
            byte[] bytesToEncrypt = ((TextView) findViewById(R.id.input)).getText().toString().getBytes(StandardCharsets.UTF_8);
            if (!crypto.getKeyInfo(SYMMETRIC_ENCRYPTION_KEY).isUserAuthenticationRequired()) {
                try {
                    future.complete(crypto.encrypt(SYMMETRIC_ENCRYPTION_KEY, bytesToEncrypt));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                crypto.encrypt(this, SYMMETRIC_ENCRYPTION_KEY, bytesToEncrypt)
                        .whenComplete((encryptionResult, throwable) -> {
                            if (null != throwable) {
                                future.completeExceptionally(throwable);
                            } else future.complete(encryptionResult);
                        });
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        future.whenComplete((encryptionResult, throwable) -> {
            if (null != throwable) {
                handleError(throwable);
                return;
            }
            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.cipherText)).setText(Hex.encodeHexString(encryptionResult.getCipherText()));
                ((TextView) findViewById(R.id.iv)).setText(Hex.encodeHexString(encryptionResult.getInitializationVector()));
            });
            setStatus("Data encrypted successfully");
        });
    }

    public void onRemoveSymmetricKeyClick(View view) {
        try {
            crypto.deleteKey(SYMMETRIC_ENCRYPTION_KEY);
            setStatus("Key removed successfully");
            updateVisibility();
        } catch (Exception e) {
            handleError(e);
        }
    }

    private void updateVisibility() {
        findViewById(R.id.authenticationRequired).setVisibility(View.INVISIBLE);
        findViewById(R.id.createSymmetricKeyButton).setVisibility(View.GONE);
        findViewById(R.id.decryptSymmetricallyButton).setVisibility(View.VISIBLE);
        findViewById(R.id.encryptSymmetricallyButton).setVisibility(View.VISIBLE);
        findViewById(R.id.removeSymmetricKeyButton).setVisibility(View.VISIBLE);
        try {
            if (!crypto.containsKey(SYMMETRIC_ENCRYPTION_KEY)) {
                findViewById(R.id.authenticationRequired).setVisibility(View.VISIBLE);
                findViewById(R.id.createSymmetricKeyButton).setVisibility(View.VISIBLE);
                findViewById(R.id.decryptSymmetricallyButton).setVisibility(View.GONE);
                findViewById(R.id.encryptSymmetricallyButton).setVisibility(View.GONE);
                findViewById(R.id.removeSymmetricKeyButton).setVisibility(View.GONE);
            }
        } catch (KeyStoreException e) {
            handleError(e);
        }
    }

    private void handleError(Throwable throwable) {
        String msg = throwable.getMessage();
        Log.e(TAG, msg, throwable);
        setStatus(null == msg ? throwable.toString() : msg);
    }

    private void setStatus(String msg) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.status)).setText(msg));
    }

    private void updateAuthenticationRequiredText() {
        SwitchCompat aSwitch = findViewById(R.id.authenticationRequired);
        String text = aSwitch.isChecked() ? getString(R.string.accessLevel_authentication_required) : getString(R.string.accessLevel_always);
        aSwitch.setText(text);
    }
}