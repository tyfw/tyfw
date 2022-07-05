package com.example.tyfw.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.tyfw.MainActivity;
import com.example.tyfw.R;
import com.example.tyfw.SearchResultsActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tyfw.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    // private Button search_button;
    // EditText firstNameEditText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // View view =  inflater.inflate(R.layout.fragment_search, container, false);

        final EditText firstNameEditText = (EditText) root.findViewById(R.id.search_input);
        final Button search_button = (Button) root.findViewById(R.id.search_button);
        // binding.searchButton;

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchResultsActivity = new Intent(getActivity(), SearchResultsActivity.class);
                searchResultsActivity.putExtra("queryString", firstNameEditText.getText().toString());
                startActivity(searchResultsActivity);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}