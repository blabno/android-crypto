package com.labnoratory.sample_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LifecycleOwner;

public abstract class AbstractEncryptFragment extends AbstractTab {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView status = view.findViewById(R.id.status);
        SwitchCompat authenticationRequiredSwitch = view.findViewById(R.id.authenticationRequired);
        TextView input = view.findViewById(R.id.input);
        TextView cipherText = view.findViewById(R.id.cipherText);
        Button createKeyButton = view.findViewById(R.id.createKeyButton);
        Button decryptButton = view.findViewById(R.id.decryptButton);
        Button encryptButton = view.findViewById(R.id.encryptButton);
        Button removeKeyButton = view.findViewById(R.id.removeKeyButton);

        AbstractEncryptViewModel model = getModel();

        LifecycleOwner owner = getViewLifecycleOwner();
        model.getStatus().observe(owner, status::setText);
        model.getAuthenticationRequired().observe(owner, authenticationRequired -> {
            String text = authenticationRequired ? getString(R.string.accessLevel_authentication_required) : getString(R.string.accessLevel_always);
            authenticationRequiredSwitch.setText(text);
        });
        model.getKeyExists().observe(owner, keyExists -> {
            authenticationRequiredSwitch.setVisibility(keyExists ? View.INVISIBLE : View.VISIBLE);
            createKeyButton.setVisibility(keyExists ? View.GONE : View.VISIBLE);
            decryptButton.setVisibility(keyExists ? View.VISIBLE : View.GONE);
            encryptButton.setVisibility(keyExists ? View.VISIBLE : View.GONE);
            removeKeyButton.setVisibility(keyExists ? View.VISIBLE : View.GONE);
        });

        input.addTextChangedListener(new TextWatcherAdapter(model.getPayload()));
        cipherText.addTextChangedListener(new TextWatcherAdapter(model.getCipherText()));
        authenticationRequiredSwitch.setOnClickListener(v -> model.getAuthenticationRequired().postValue(authenticationRequiredSwitch.isChecked()));
        createKeyButton.setOnClickListener(v -> model.createKey());
        removeKeyButton.setOnClickListener(v -> model.removeKey());
    }

    @NonNull
    protected abstract AbstractEncryptViewModel getModel() ;

}
