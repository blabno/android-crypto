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
    private static final String ASYMMETRIC_ENCRYPTION_KEY = "asymmetric-encryption";
    private static final String SYMMETRIC_ENCRYPTION_KEY = "symmetric-encryption";

    private final AndroidCrypto crypto = new AndroidCrypto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateVisibility();
        updateAsymmetricKeyRequiresAuthenticationText();
        updateSymmetricKeyRequiresAuthenticationText();
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

    public void onSymmetricKeyRequiresAuthenticationClick(View view) {
        updateSymmetricKeyRequiresAuthenticationText();
    }

    public void onAsymmetricKeyRequiresAuthenticationClick(View view) {
        updateAsymmetricKeyRequiresAuthenticationText();
    }

    public void onCreateSymmetricKeyClick(View view) {
        try {
            boolean authenticationRequired = ((SwitchCompat) findViewById(R.id.symmetricKeyRequiresAuthentication)).isChecked();
            AndroidCrypto.AccessLevel accessLevel = authenticationRequired ? AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED : AndroidCrypto.AccessLevel.ALWAYS;
            crypto.createSymmetricEncryptionKey(SYMMETRIC_ENCRYPTION_KEY, accessLevel, false);
            setStatus("Symmetric encryption key created successfully");
            updateVisibility();
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void onCreateAsymmetricKeyClick(View view) {
        try {
            boolean authenticationRequired = ((SwitchCompat) findViewById(R.id.asymmetricKeyRequiresAuthentication)).isChecked();
            AndroidCrypto.AccessLevel accessLevel = authenticationRequired ? AndroidCrypto.AccessLevel.AUTHENTICATION_REQUIRED : AndroidCrypto.AccessLevel.ALWAYS;
            crypto.createAsymmetricEncryptionKey(ASYMMETRIC_ENCRYPTION_KEY, accessLevel, false);
            setStatus("Asymmetric encryption key created successfully");
            updateVisibility();
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void onDecryptAsymmetricallyClick(View view) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            byte[] cipherText = Hex.decodeHex(((TextView) findViewById(R.id.cipherText)).getText().toString());
            String alias = ASYMMETRIC_ENCRYPTION_KEY;
            if (!crypto.getKeyInfo(alias).isUserAuthenticationRequired()) {
                try {
                    future.complete(crypto.decryptAsymmetrically(alias, cipherText));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                crypto.decryptAsymmetrically(this, alias, cipherText)
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

    public void onDecryptSymmetricallyClick(View view) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            byte[] cipherText = Hex.decodeHex(((TextView) findViewById(R.id.cipherText)).getText().toString());
            byte[] iv = Hex.decodeHex(((TextView) findViewById(R.id.iv)).getText().toString());
            String alias = SYMMETRIC_ENCRYPTION_KEY;
            if (!crypto.getKeyInfo(alias).isUserAuthenticationRequired()) {
                try {
                    future.complete(crypto.decryptSymmetrically(alias, cipherText, iv));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                crypto.decryptSymmetrically(this, alias, cipherText, iv)
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

    public void onDecryptSymmetricallyWithPasswordClick(View view) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();
        try {
            byte[] password = "Hello World!".getBytes(StandardCharsets.UTF_8);
            byte[] salt = "?!?".getBytes(StandardCharsets.UTF_8);
            int iterations = 1024;
            byte[] cipherText = Hex.decodeHex(((TextView) findViewById(R.id.cipherText)).getText().toString());
            byte[] iv = Hex.decodeHex(((TextView) findViewById(R.id.iv)).getText().toString());
            try {
                future.complete(crypto.decryptSymmetricallyWithPassword(password, salt, iterations, cipherText, iv));
            } catch (Exception e) {
                future.completeExceptionally(e);
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

    public void onEncryptAsymmetricallyClick(View view) {
        try {
            byte[] bytesToEncrypt = ((TextView) findViewById(R.id.input)).getText().toString().getBytes(StandardCharsets.UTF_8);
            byte[] cipherText = crypto.encryptAsymmetrically(ASYMMETRIC_ENCRYPTION_KEY, bytesToEncrypt);
            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.cipherText)).setText(Hex.encodeHexString(cipherText));
                ((TextView) findViewById(R.id.iv)).setText("");
            });
            setStatus("Data encrypted successfully");
        } catch (Exception e) {
            handleError(e);
        }
    }

    public void onEncryptSymmetricallyClick(View view) {
        CompletableFuture<EncryptionResult> future = new CompletableFuture<>();
        try {
            byte[] bytesToEncrypt = ((TextView) findViewById(R.id.input)).getText().toString().getBytes(StandardCharsets.UTF_8);
            String alias = SYMMETRIC_ENCRYPTION_KEY;
            if (!crypto.getKeyInfo(alias).isUserAuthenticationRequired()) {
                try {
                    future.complete(crypto.encryptSymmetrically(alias, bytesToEncrypt));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            } else {
                crypto.encryptSymmetrically(this, alias, bytesToEncrypt)
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

    public void onEncryptSymmetricallyWithPasswordClick(View view) {
        CompletableFuture<EncryptionResult> future = new CompletableFuture<>();
        try {
            byte[] password = "Hello World!".getBytes(StandardCharsets.UTF_8);
            byte[] salt = "?!?".getBytes(StandardCharsets.UTF_8);
            int iterations = 1024;
            byte[] bytesToEncrypt = ((TextView) findViewById(R.id.input)).getText().toString().getBytes(StandardCharsets.UTF_8);
            try {
                future.complete(crypto.encryptSymmetricallyWithPassword(password, salt, iterations, bytesToEncrypt));
            } catch (Exception e) {
                future.completeExceptionally(e);
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

    public void onRemoveAsymmetricKeyClick(View view) {
        try {
            crypto.deleteKey(ASYMMETRIC_ENCRYPTION_KEY);
            setStatus("Key removed successfully");
            updateVisibility();
        } catch (Exception e) {
            handleError(e);
        }
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
        findViewById(R.id.symmetricKeyRequiresAuthentication).setVisibility(View.INVISIBLE);
        findViewById(R.id.createSymmetricKeyButton).setVisibility(View.GONE);
        findViewById(R.id.decryptSymmetricallyButton).setVisibility(View.VISIBLE);
        findViewById(R.id.encryptSymmetricallyButton).setVisibility(View.VISIBLE);
        findViewById(R.id.removeSymmetricKeyButton).setVisibility(View.VISIBLE);
        try {
            if (!crypto.containsKey(SYMMETRIC_ENCRYPTION_KEY)) {
                findViewById(R.id.symmetricKeyRequiresAuthentication).setVisibility(View.VISIBLE);
                findViewById(R.id.createSymmetricKeyButton).setVisibility(View.VISIBLE);
                findViewById(R.id.decryptSymmetricallyButton).setVisibility(View.GONE);
                findViewById(R.id.encryptSymmetricallyButton).setVisibility(View.GONE);
                findViewById(R.id.removeSymmetricKeyButton).setVisibility(View.GONE);
            }
        } catch (KeyStoreException e) {
            handleError(e);
        }
        findViewById(R.id.asymmetricKeyRequiresAuthentication).setVisibility(View.INVISIBLE);
        findViewById(R.id.createAsymmetricKeyButton).setVisibility(View.GONE);
        findViewById(R.id.decryptAsymmetricallyButton).setVisibility(View.VISIBLE);
        findViewById(R.id.encryptAsymmetricallyButton).setVisibility(View.VISIBLE);
        findViewById(R.id.removeAsymmetricKeyButton).setVisibility(View.VISIBLE);
        try {
            if (!crypto.containsKey(ASYMMETRIC_ENCRYPTION_KEY)) {
                findViewById(R.id.asymmetricKeyRequiresAuthentication).setVisibility(View.VISIBLE);
                findViewById(R.id.createAsymmetricKeyButton).setVisibility(View.VISIBLE);
                findViewById(R.id.decryptAsymmetricallyButton).setVisibility(View.GONE);
                findViewById(R.id.encryptAsymmetricallyButton).setVisibility(View.GONE);
                findViewById(R.id.removeAsymmetricKeyButton).setVisibility(View.GONE);
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

    private void updateAsymmetricKeyRequiresAuthenticationText() {
        SwitchCompat aSwitch = findViewById(R.id.asymmetricKeyRequiresAuthentication);
        String text = aSwitch.isChecked() ? getString(R.string.accessLevel_authentication_required) : getString(R.string.accessLevel_always);
        aSwitch.setText(text);
    }

    private void updateSymmetricKeyRequiresAuthenticationText() {
        SwitchCompat aSwitch = findViewById(R.id.symmetricKeyRequiresAuthentication);
        String text = aSwitch.isChecked() ? getString(R.string.accessLevel_authentication_required) : getString(R.string.accessLevel_always);
        aSwitch.setText(text);
    }
}