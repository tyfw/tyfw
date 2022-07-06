package com.example.tyfw.ui.leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.databinding.FragmentLeaderboardBinding;
import com.example.tyfw.ui.profile.ProfileActivity;
import com.example.tyfw.ui.profile.WalletProfileActivity;
import com.example.tyfw.utils.LeaderboardListAdapter;
import com.example.tyfw.utils.LeaderboardRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private List<LeaderboardRow> itemsList = new ArrayList<LeaderboardRow>();
    private ListView listView;
    private LeaderboardListAdapter adapter;
    private FragmentLeaderboardBinding binding;

    private String TAG = "LEADERBOARD";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel leaderboardViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = (ListView) binding.list;
        adapter = new LeaderboardListAdapter(root.getContext(), itemsList);
        listView.setAdapter(adapter);

        // Call the leaderboard API

        App config = (App) getActivity().getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken", config.getGoogleIdToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetLeaderboard getAuth = new GetLeaderboard(jsonObject);
        Thread getAuthThread = new Thread(getAuth);
        getAuthThread.start();
        try {
            getAuthThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONArray serverResponse = getAuth.getValue();
        Log.e(TAG, serverResponse.toString());
        for (int i = 0; i < serverResponse.length(); i++) {
            try {
                JSONObject currFriend = serverResponse.getJSONObject(i);
                LeaderboardRow items = new LeaderboardRow();
                items.setName(currFriend.getString("user"));
                items.setValue(String.valueOf(currFriend.getDouble("value")));
                itemsList.add(items);
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Followed this SOF post: https://stackoverflow.com/questions/32827787/intent-in-listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = adapterView.getItemAtPosition(i).toString();

                Intent intent;
                if (isProfile(item)){
                    intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.putExtra("username", item);
                } else {
                    intent = new Intent(getActivity(), WalletProfileActivity.class);
                    intent.putExtra("walletAddress", item);
                }
                startActivity(intent);

            }

            // TODO: make this a valid profile checker
            private boolean isProfile(@NonNull String s){
                Log.d(TAG, s);
                return s.length() < 2;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class GetLeaderboard implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private JSONArray value;
        private JSONObject jsonObject;
        private String url = "http://34.105.106.85:8081/user/leaderboard/";

        public GetLeaderboard(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("googleIdToken", jsonObject.getString("googleIdToken"))
                        .setPriority(Priority.MEDIUM)
                        .build();

                ANResponse<JSONArray> response = request.executeForJSONArray();

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

        public JSONArray getValue() {
            return value;
        }
    }
}