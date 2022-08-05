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

import com.example.tyfw.App;
import com.example.tyfw.api.APICallers;
import com.example.tyfw.databinding.FragmentSocialBinding;
import com.example.tyfw.utils.SocialListAdapter;
import com.example.tyfw.utils.SocialRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment {

    private FragmentSocialBinding binding;
    private ListView listView;
    private final List<SocialRow> itemsList = new ArrayList<SocialRow>();
    private String TAG = "LEADERBOARD";

    //start of leaderboard paste
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // SocialViewModel socialViewModel = new ViewModelProvider(this).get(SocialViewModel.class);

        binding = FragmentSocialBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = (ListView) binding.list;
        SocialListAdapter adapter = new SocialListAdapter(root.getContext(), itemsList);
        listView.setAdapter(adapter);

        // Call the get friends API
        App config = (App) getActivity().getApplicationContext();

        APICallers.GetFriends getFriends = new APICallers.GetFriends(config.getEmail());
        Thread getAuthThread = new Thread(getFriends);
        getAuthThread.start();
        try {
            getAuthThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject serverResponse = getFriends.getValue();
        JSONArray friendsList = new JSONArray();
        try {
            friendsList = serverResponse.getJSONArray("friends");

            if (friendsList == null) {
                Toast.makeText(getContext(), "Unable to access your friends, please retry.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, serverResponse.toString());

                SocialRow firstItem = new SocialRow();
                firstItem.setName("Friends");
                itemsList.add(firstItem);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < friendsList.length(); i++) {
                    try {
                        String currFriend = friendsList.getString(i);
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
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Unable to access your friends, please retry.", Toast.LENGTH_SHORT).show();
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
                    chatActivity.putExtra("email", config.getEmail());
                    chatActivity.putExtra("googleIdToken", config.getGoogleIdToken());
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

}
