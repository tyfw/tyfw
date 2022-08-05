package com.example.tyfw.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tyfw.R;

import java.util.List;

public class SocialListAdapter extends BaseAdapter {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private final Context context;
    private LayoutInflater inflater;
    private final List<SocialRow> socialValues;

    public SocialListAdapter(Context context, List<SocialRow> socialValues) {
        this.socialValues = socialValues;
        this.context = context;
    }

    @Override
    public int getCount() { return socialValues.size(); }

    @Override
    public Object getItem(int position) { return socialValues.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tempView = convertView;
        SocialRowHolder userPair;
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (tempView == null) {
            tempView = inflater.inflate(R.layout.social_row, parent, false);
            userPair = new SocialRowHolder();
            userPair.name =(TextView) tempView.findViewById(R.id.social_friend_name);
            tempView.setTag(tempView);
        } else {
            userPair = (SocialRowHolder) tempView.getTag();
        }

        final SocialRow val = socialValues.get(position);
        userPair.name.setText(val.getName());

        return tempView;
    }

    static class SocialRowHolder {
        TextView name;
        TextView value;
    }
}
