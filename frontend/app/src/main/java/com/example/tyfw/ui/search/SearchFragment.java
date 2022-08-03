package com.example.tyfw.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.SearchResultsActivity;

import com.example.tyfw.databinding.FragmentSearchBinding;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;

public class SearchFragment extends Fragment {

    private final String TAG = "SEARCH";

    private FragmentSearchBinding binding;
    private EditText search_input;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button search_button = (Button) root.findViewById(R.id.search_button);
        search_input = (EditText) root.findViewById(R.id.search_input);

        search_button.setOnClickListener(v -> {
            String queryString = search_input.getText().toString();

            Log.d(TAG,queryString);
            App config = (App) getContext().getApplicationContext();

            SearchCalls.GetSearch getSearch = new SearchCalls.GetSearch(queryString, config.getEmail(), config.getGoogleIdToken());
            Thread getAuthThread = new Thread(getSearch);
            getAuthThread.start();
            try {
                getAuthThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject serverResponse = getSearch.getValue();
            Log.e("a", String.valueOf(serverResponse));

            if (serverResponse == null) {
                Toast.makeText(getContext(), "No users found with the requested search", Toast.LENGTH_LONG).show();
                return;
            }
            if (serverResponse.length() > 0) {
                Intent searchResultsActivity = new Intent(getActivity(), SearchResultsActivity.class);
                searchResultsActivity.putExtra("queryString", queryString);
                searchResultsActivity.putExtra("serverResponse", serverResponse.toString());
                startActivity(searchResultsActivity);
            } else {
                Intent searchResultsActivity = new Intent(getActivity(), SearchResultsActivity.class);
                searchResultsActivity.putExtra("queryString", queryString);
                searchResultsActivity.putExtra("serverResponse", (new JSONArray()).toString());
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