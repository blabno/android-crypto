package com.labnoratory.sample_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import static com.labnoratory.sample_app.OneTimeObserver.observeOnce;

public class EncryptSymmetricallyWithPasswordFragment extends Fragment {

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
        iterations.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Integer i = Integer.parseInt(s.toString());
                    if (!i.equals(model.getIterations().getValue())) {
                        model.getIterations().postValue(i);
                    }
                } catch (Exception ignore) {
                    model.getStatus().postValue("Iterations must be a number");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

}
