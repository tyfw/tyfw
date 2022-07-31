package com.example.tyfw.ui.leaderboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.MainActivity;
import com.example.tyfw.databinding.FragmentLeaderboardBinding;
import com.example.tyfw.ui.profile.ProfileActivity;
import com.example.tyfw.utils.LeaderboardListAdapter;
import com.example.tyfw.utils.LeaderboardRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LeaderboardFragment extends Fragment {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private final List<LeaderboardRow> itemsList = new ArrayList<LeaderboardRow>();
    private ListView listView;
    private FragmentLeaderboardBinding binding;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private String TAG = "LEADERBOARD";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        // leaderboardViewModel.notifyAll();

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = (ListView) binding.list;
        LeaderboardListAdapter adapter = new LeaderboardListAdapter(root.getContext(), itemsList);
        listView.setAdapter(adapter);

        // Call the leaderboard API
        App config = (App) getActivity().getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken",  config.getGoogleIdToken());
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
        if (serverResponse == null) {
            Toast.makeText(getContext(), "Unable to access leaderboard, please retry.", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, serverResponse.toString());

            LeaderboardRow firstItem = new LeaderboardRow();
            firstItem.setName("Username or wallet address");
            firstItem.setValue("Change YOY (%)");
            itemsList.add(firstItem);
            adapter.notifyDataSetChanged();

            for (int i = 0; i < serverResponse.length(); i++) {
                try {
                    JSONObject currFriend = serverResponse.getJSONObject(i);
                    LeaderboardRow items = new LeaderboardRow();

                    try{
                        items.setValue(df.format(currFriend.getDouble("value")) + "%");
                    } catch (JSONException e) {
                        items.setValue(df.format(0.0) + "%");
                    }

                    items.setName(currFriend.getString("user"));
                    items.setAddress(currFriend.getString("address"));
                    itemsList.add(items);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unable to access leaderboard element", Toast.LENGTH_SHORT).show();
                }
            }
            adapter.notifyDataSetChanged();
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "VIEW CREATED");

        // Followed this SOF post: https://stackoverflow.com/questions/32827787/intent-in-listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    LeaderboardRow item;

                    item = (LeaderboardRow) adapterView.getItemAtPosition(i);

                    App config = (App) getActivity().getApplicationContext();

                    if (config.getUsername() != null) {
                        if (Objects.equals(config.getUsername(), item.getName())){
                            Intent mainActivity;
                            mainActivity = new Intent(getActivity(), MainActivity.class);
                            mainActivity.putExtra("email", config.getEmail());
                            mainActivity.putExtra("googleIdToken", config.getGoogleIdToken());
                            startActivity(mainActivity);
                        } else {
                            Log.e("Here", config.getUsername());
                            Log.e("Here", item.getName());
                            Intent profileActivity = new Intent(getActivity(), ProfileActivity.class);
                            profileActivity.putExtra("username", item.getName());
                            profileActivity.putExtra("walletAddress", item.getAddress());
                            startActivity(profileActivity);
                        }
                    } else {
                        Intent profileActivity = new Intent(getActivity(), ProfileActivity.class);
                        profileActivity.putExtra("username", item.getName());
                        profileActivity.putExtra("walletAddress", item.getAddress());
                        startActivity(profileActivity);
                    }
                }
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
        private final JSONObject jsonObject;

        public GetLeaderboard(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                Log.e("a", jsonObject.toString());
                String url = "http://34.105.106.85:8081/user/leaderboard/";
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
                    Log.e("Leaderboard", String.valueOf(error.getErrorCode()));
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