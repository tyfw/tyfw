package com.example.tyfw.ui.settings;

import com.example.tyfw.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tyfw.databinding.FragmentSettingsBinding;
import com.example.tyfw.ui.profile.ProfileActivity;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    private Button myProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        myProfile = view.findViewById(R.id.my_profile_button);

        myProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("settings", "Checking user's own profile.");
                Intent myProfileIntent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(myProfileIntent);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}