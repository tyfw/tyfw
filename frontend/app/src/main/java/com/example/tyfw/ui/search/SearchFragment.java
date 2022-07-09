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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;

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
                jsonObject.put("email", config.getEmail());
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

            if (serverResponse == null) {
                Toast.makeText(getContext(), "Unable to get search results, please try again", Toast.LENGTH_LONG).show();
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
                        .addHeaders("email", jsonObject.getString("email"))
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
                    Toast.makeText(getContext(), "Unable to obtain search results. Please try again",Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

                Toast.makeText(getContext(), "Unable to obtain search results. Please try again",Toast.LENGTH_LONG).show();
            }
        }

        public JSONObject getValue() {
            return value;
        }
    }
}