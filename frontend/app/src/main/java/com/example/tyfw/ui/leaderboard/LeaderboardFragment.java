package com.example.tyfw.ui.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tyfw.databinding.FragmentLeaderboardBinding;
import com.example.tyfw.utils.LeaderboardListAdapter;
import com.example.tyfw.utils.LeaderboardRow;

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LeaderboardViewModel leaderboardViewModel =
                new ViewModelProvider(this).get(LeaderboardViewModel.class);

        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*
        final TextView textView = binding.textLeaderboard;
        leaderboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
        */

        listView = (ListView) binding.list;
        adapter = new LeaderboardListAdapter(root.getContext(), itemsList);
        listView.setAdapter(adapter);

        for (int i = 0; i < 10; i++) {
            LeaderboardRow items = new LeaderboardRow();

            items.setName(Integer.toString(i));
            items.setValue(Integer.toString(i));

            itemsList.add(items);
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}