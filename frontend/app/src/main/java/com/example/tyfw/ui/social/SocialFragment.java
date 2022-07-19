package com.example.tyfw.ui.social;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tyfw.SearchResultsActivity;
import com.example.tyfw.databinding.FragmentSocialBinding;

public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SocialViewModel socialViewModel =
                new ViewModelProvider(this).get(SocialViewModel.class);

        binding = FragmentSocialBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textSocial;
//        socialViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        String friend = "Zeph";
        Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
        chatActivity.putExtra("name", friend);
        startActivity(chatActivity);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}