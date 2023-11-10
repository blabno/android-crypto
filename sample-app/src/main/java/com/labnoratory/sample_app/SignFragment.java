package com.labnoratory.sample_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import static com.labnoratory.sample_app.OneTimeObserver.observeOnce;

public class SignFragment extends AbstractTab {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView status = view.findViewById(R.id.status);
        SwitchCompat authenticationRequiredSwitch = view.findViewById(R.id.authenticationRequired);
        TextView input = view.findViewById(R.id.input);
        TextView publicKey = view.findViewById(R.id.publicKey);
        TextView signature = view.findViewById(R.id.signature);
        Button createKeyButton = view.findViewById(R.id.createKeyButton);
        Button signButton = view.findViewById(R.id.signButton);
        Button verifySignatureButton = view.findViewById(R.id.verifySignatureButton);
        Button removeKeyButton = view.findViewById(R.id.removeKeyButton);

        SignViewModel model = new ViewModelProvider(this).get(SignViewModel.class);

        LifecycleOwner owner = getViewLifecycleOwner();
        model.getStatus().observe(owner, status::setText);
        model.getAuthenticationRequired().observe(owner, authenticationRequired -> {
            String text = authenticationRequired ? getString(R.string.accessLevel_authentication_required) : getString(R.string.accessLevel_always);
            authenticationRequiredSwitch.setText(text);
        });
        model.getKeyExists().observe(owner, keyExists -> {
            authenticationRequiredSwitch.setVisibility(keyExists ? View.INVISIBLE : View.VISIBLE);
            createKeyButton.setVisibility(keyExists ? View.GONE : View.VISIBLE);
            signButton.setVisibility(keyExists ? View.VISIBLE : View.GONE);
            removeKeyButton.setVisibility(keyExists ? View.VISIBLE : View.GONE);
        });

        input.addTextChangedListener(new TextWatcherAdapter(model.getPayload()));
        publicKey.addTextChangedListener(new TextWatcherAdapter(model.getPublicKey()));
        signature.addTextChangedListener(new TextWatcherAdapter(model.getSignature()));
        authenticationRequiredSwitch.setOnClickListener(v -> model.getAuthenticationRequired().postValue(authenticationRequiredSwitch.isChecked()));
        createKeyButton.setOnClickListener(v -> model.createKey());
        removeKeyButton.setOnClickListener(v -> model.removeKey());

        signButton.setOnClickListener(_v -> {
            observeOnce(model.getSignature(), v -> requireActivity().runOnUiThread(() -> signature.setText(v)));
            observeOnce(model.getPublicKey(), v -> requireActivity().runOnUiThread(() -> publicKey.setText(v)));
            model.sign(requireActivity());
        });
        verifySignatureButton.setOnClickListener(v -> model.verifySignature());
    }

    @Override
    public int getTitle() {
        return R.string.sign;
    }

}
