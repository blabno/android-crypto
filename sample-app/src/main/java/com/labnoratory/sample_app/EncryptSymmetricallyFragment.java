package com.labnoratory.sample_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import static com.labnoratory.sample_app.OneTimeObserver.observeOnce;

public class EncryptSymmetricallyFragment extends AbstractEncryptFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.encrypt_symmetrically, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView cipherText = view.findViewById(R.id.cipherText);
        TextView iv = view.findViewById(R.id.iv);
        Button decryptButton = view.findViewById(R.id.decryptButton);
        Button encryptButton = view.findViewById(R.id.encryptButton);

        EncryptSymmetricallyViewModel model = getModel();

        iv.addTextChangedListener(new TextWatcherAdapter(model.getIv()));
        encryptButton.setOnClickListener(_v -> {
            observeOnce(model.getCipherText(), v -> requireActivity().runOnUiThread(() -> cipherText.setText(v)));
            observeOnce(model.getIv(), v -> requireActivity().runOnUiThread(() -> iv.setText(v)));
            model.encrypt(requireActivity());
        });
        decryptButton.setOnClickListener(v -> model.decrypt(requireActivity()));
    }

    @NonNull
    @Override
    protected EncryptSymmetricallyViewModel getModel() {
        return new ViewModelProvider(this).get(EncryptSymmetricallyViewModel.class);
    }

}
