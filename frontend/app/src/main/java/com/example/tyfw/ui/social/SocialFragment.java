package com.example.tyfw.ui.social;

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
import com.example.tyfw.databinding.FragmentSocialBinding;
import com.example.tyfw.ui.leaderboard.LeaderboardFragment;
import com.example.tyfw.ui.profile.ProfileActivity;
import com.example.tyfw.utils.LeaderboardListAdapter;
import com.example.tyfw.utils.LeaderboardRow;
import com.example.tyfw.utils.SocialListAdapter;
import com.example.tyfw.utils.SocialRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;
    private ListView listView;
    private final List<SocialRow> itemsList = new ArrayList<SocialRow>();
    private String TAG = "LEADERBOARD";

    //start of leaderboard paste
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SocialViewModel socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);

        // leaderboardViewModel.notifyAll();

        binding = FragmentSocialBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = (ListView) binding.list;
        SocialListAdapter adapter = new SocialListAdapter(root.getContext(), itemsList);
        listView.setAdapter(adapter);

        // Call the get friends API
        App config = (App) getActivity().getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken",  config.getGoogleIdToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetFriendsList getAuth = new SocialFragment.GetFriendsList(jsonObject);
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

            SocialRow firstItem = new SocialRow();
            firstItem.setName("Friends");
            itemsList.add(firstItem);
            adapter.notifyDataSetChanged();

            for (int i = 0; i < serverResponse.length(); i++) {
                try {
                    String currFriend = serverResponse.getString(i);
                    SocialRow items = new SocialRow();

                    items.setName(currFriend);
                    itemsList.add(items);
                    adapter.notifyDataSetChanged();

                    // TODO: add onclick listener for itemlists
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unable to access social element", Toast.LENGTH_SHORT).show();
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
            // TODO: set up onclick for the whole row
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0){
                    SocialRow item;

                    item = (SocialRow) adapterView.getItemAtPosition(i);

                    App config = (App) getActivity().getApplicationContext();

                    Log.e("Here", config.getUsername());
                    Log.e("Here", item.getName());
                    Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
                    chatActivity.putExtra("fromUser", config.getUsername());
                    chatActivity.putExtra("toUser", item.getName());
                    startActivity(chatActivity);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class GetFriendsList implements Runnable {
        final static String TAG = "GetAuthRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetFriendsList(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
//                String url = "http://localhost:8081/user/getfriends/";
                String url = "http://34.105.106.85:8081/user/getfriends/";
                ANRequest request = AndroidNetworking.get(url)
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
                    Log.e("Social", String.valueOf(error.getErrorCode()));
                    error.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONArray getValue() {
            try {
                return value.getJSONArray("friends");
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONArray();
            }
        }
    }
}

//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        SocialViewModel socialViewModel =
//                new ViewModelProvider(this).get(SocialViewModel.class);
//
//        binding = FragmentSocialBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
////        final TextView textView = binding.textSocial;
////        socialViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        String friend = "Zeph";
//        Intent chatActivity = new Intent(getActivity(), ChatActivity.class);
//        chatActivity.putExtra("name", friend);
//        startActivity(chatActivity);
//        return root;
//    }
