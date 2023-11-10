package com.labnoratory.sample_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import static com.labnoratory.sample_app.OneTimeObserver.observeOnce;

public class EncryptSymmetricallyWithPasswordFragment extends AbstractTab {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.encrypt_symmetrically_with_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = requireActivity();
        TextView status = view.findViewById(R.id.status);
        TextView input = view.findViewById(R.id.input);
        TextView cipherText = view.findViewById(R.id.cipherText);
        TextView iv = view.findViewById(R.id.iv);
        TextView iterations = view.findViewById(R.id.iterations);
        TextView password = view.findViewById(R.id.password);
        TextView salt = view.findViewById(R.id.salt);
        Button decryptButton = view.findViewById(R.id.decryptButton);
        Button encryptButton = view.findViewById(R.id.encryptButton);

        LifecycleOwner owner = getViewLifecycleOwner();
        EncryptSymmetricallyWithPasswordViewModel model = new ViewModelProvider(this).get(EncryptSymmetricallyWithPasswordViewModel.class);
        model.getStatus().observe(owner, status::setText);

        cipherText.addTextChangedListener(new TextWatcherAdapter(model.getCipherText()));
        iv.addTextChangedListener(new TextWatcherAdapter(model.getIv()));
        iterations.addTextChangedListener(new TextWatcherAdapter(model.getIterations()));
        input.addTextChangedListener(new TextWatcherAdapter(model.getPayload()));
        password.addTextChangedListener(new TextWatcherAdapter(model.getPassword()));
        salt.addTextChangedListener(new TextWatcherAdapter(model.getSalt()));
        encryptButton.setOnClickListener(_v -> {
            observeOnce(model.getCipherText(), v -> activity.runOnUiThread(() -> cipherText.setText(v)));
            observeOnce(model.getIv(), v -> activity.runOnUiThread(() -> iv.setText(v)));
            model.encrypt();
        });
        decryptButton.setOnClickListener(v -> model.decrypt());
    }

    @Override
    public int getTitle() {
        return R.string.encryptSymmetricallyWithPassword;
    }

}
