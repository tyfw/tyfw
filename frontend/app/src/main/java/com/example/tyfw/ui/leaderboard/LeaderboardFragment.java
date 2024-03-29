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

import com.example.tyfw.App;
import com.example.tyfw.MainActivity;
import com.example.tyfw.api.APICallers;
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
    private LeaderboardListAdapter adapter;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    private String TAG = "LEADERBOARD";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // LeaderboardViewModel leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.list;
        adapter = new LeaderboardListAdapter(root.getContext(), itemsList);
        listView.setAdapter(adapter);

        setLeaderBoard();
        return root;
    }

    private void setLeaderBoard() {
        // Call the leaderboard API
        App config = (App) getActivity().getApplicationContext();

        APICallers.GetLeaderboard getAuth = new APICallers.GetLeaderboard(config.getEmail(), config.getGoogleIdToken());
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


}