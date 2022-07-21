package com.example.tyfw.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tyfw.R;

import java.util.List;

public class LeaderboardListAdapter extends BaseAdapter {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private final Context context;
    private LayoutInflater inflater;
    private final List<LeaderboardRow> leaderboardValues;

    public LeaderboardListAdapter(Context context, List<LeaderboardRow> leaderboardValues) {
        this.leaderboardValues = leaderboardValues;
        this.context = context;
    }

    @Override
    public int getCount() { return leaderboardValues.size(); }

    @Override
    public Object getItem(int position) { return leaderboardValues.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeaderboardRowHolder userPair;
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.leaderboard_row, parent, false);
            userPair = new LeaderboardRowHolder();
            userPair.name =(TextView) convertView.findViewById(R.id.leaderboard_friend_name);
            userPair.value =(TextView) convertView.findViewById(R.id.leaderboard_friend_value);
            convertView.setTag(convertView);
        } else {
            userPair = (LeaderboardRowHolder) convertView.getTag();
        }

        final LeaderboardRow val = leaderboardValues.get(position);
        userPair.name.setText(val.getName());
        userPair.value.setText(val.getValue());

        return convertView;
    }

    static class LeaderboardRowHolder {
        TextView name;
        TextView value;
    }
}
