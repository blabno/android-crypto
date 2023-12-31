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

public class AuthenticateFragment extends AbstractTab {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.authenticate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView status = view.findViewById(R.id.status);
        Button authenticateButton = view.findViewById(R.id.authenticateButton);
        Button clearButton = view.findViewById(R.id.clearButton);

        AuthenticateViewModel model = new ViewModelProvider(this).get(AuthenticateViewModel.class);
        model.getStatus().observe(getViewLifecycleOwner(), status::setText);

        authenticateButton.setOnClickListener(v -> model.authenticate(requireActivity()));
        clearButton.setOnClickListener(v -> model.clear());
    }

    @Override
    public int getTitle() {
        return R.string.authenticate;
    }
}
