package com.labnoratory.sample_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.labnoratory.android_crypto.Authenticator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    private void handleError(Throwable throwable) {
        String msg = throwable.getMessage();
        setStatus(null == msg ? throwable.toString() : msg);
    }

    private void setStatus(String msg) {
        runOnUiThread(() -> ((TextView) findViewById(R.id.status)).setText(msg));
    }
}