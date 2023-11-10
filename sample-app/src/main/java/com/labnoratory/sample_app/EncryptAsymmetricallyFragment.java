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
import androidx.lifecycle.ViewModelProvider;

import static com.labnoratory.sample_app.OneTimeObserver.observeOnce;

public class EncryptAsymmetricallyFragment extends AbstractEncryptFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.encrypt_asymmetrically, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = requireActivity();
        EncryptAsymmetricallyViewModel model = getModel();
        TextView cipherText = view.findViewById(R.id.cipherText);
        Button decryptButton = view.findViewById(R.id.decryptButton);
        Button encryptButton = view.findViewById(R.id.encryptButton);

        cipherText.addTextChangedListener(new TextWatcherAdapter(model.getCipherText()));

        encryptButton.setOnClickListener(_v -> {
            observeOnce(model.getCipherText(), v -> activity.runOnUiThread(() -> cipherText.setText(v)));
            model.encrypt();
        });
        decryptButton.setOnClickListener(v -> model.decrypt(activity));
    }

    @NonNull
    @Override
    protected EncryptAsymmetricallyViewModel getModel() {
        return new ViewModelProvider(this).get(EncryptAsymmetricallyViewModel.class);
    }

    @Override
    public int getTitle() {
        return R.string.encryptAsymmetrically;
    }

}
