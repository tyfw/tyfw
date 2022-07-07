package com.example.tyfw.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.SearchResultsActivity;

import com.example.tyfw.databinding.FragmentSearchBinding;
import com.example.tyfw.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFragment extends Fragment {

    private String TAG = "SEARCH";

    private FragmentSearchBinding binding;
    private Button search_button;
    private EditText search_input;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        search_button = (Button) root.findViewById(R.id.search_button);
        search_input = (EditText) root.findViewById(R.id.search_input);

        search_button.setOnClickListener(v -> {
            String queryString = search_input.getText().toString();

            Log.d(TAG,queryString);
            App config = (App) getContext().getApplicationContext();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("queryString", queryString);
                jsonObject.put("googleIdToken", config.getGoogleIdToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            GetSearch getSearch = new GetSearch(jsonObject);
            Thread getAuthThread = new Thread(getSearch);
            getAuthThread.start();
            try {
                getAuthThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject serverResponse = getSearch.getValue();
            Log.e("a", String.valueOf(serverResponse));
            try {
                if (serverResponse == null) {
                    Intent searchResultsActivity = new Intent(getActivity(), SearchResultsActivity.class);
                    searchResultsActivity.putExtra("queryString", "");
                    searchResultsActivity.putExtra("serverResponse", "");
                    startActivity(searchResultsActivity);
                }
                Log.e("a", String.valueOf(serverResponse.getJSONArray("queryMatches").length()));
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class GetSearch implements Runnable {
        private JSONObject value;
        private String url = "http://34.105.106.85:8081/user/search/";
        private JSONObject jsonObject;

        public GetSearch(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("queryString", jsonObject.getString("queryString"))
                        .addHeaders("googleIdToken", jsonObject.getString("googleIdToken"))
                        .setPriority(Priority.MEDIUM)
                        .build();
                ANResponse<JSONObject> response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    error.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONObject getValue() {
            return value;
        }
    }
}