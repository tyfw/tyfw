package com.example.tyfw.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tyfw.R;

import java.util.List;

public class SearchResultsListAdapter extends BaseAdapter {
    // This code is taken from and adapted from (this includes relevant .xml files):
    // https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
    // https://stackoverflow.com/questions/60478873/make-a-leaderboard-using-a-listview

    private Context context;
    private LayoutInflater inflater;
    private List<SearchResultsRow> searchResultsValues;

    public SearchResultsListAdapter(Context context, List<SearchResultsRow> searchResultsValues) {
        this.searchResultsValues = searchResultsValues;
        this.context = context;
    }

    @Override
    public int getCount() { return searchResultsValues.size(); }

    @Override
    public Object getItem(int position) { return searchResultsValues.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View tempView = convertView;
        SearchResultRowHolder userPair;
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (tempView == null) {
            tempView = inflater.inflate(R.layout.search_result_row, parent, false);
            userPair = new SearchResultRowHolder();
            userPair.username = (TextView) tempView.findViewById(R.id.search_result_username);
            userPair.address = (TextView) tempView.findViewById(R.id.search_result_wallet);
            tempView.setTag(tempView);
        } else {
            userPair = (SearchResultRowHolder) tempView.getTag();
        }

        final SearchResultsRow val = searchResultsValues.get(position);
        userPair.username.setText(val.getUsername());
        userPair.address.setText(val.getWallet());

        return tempView;
    }

    static class SearchResultRowHolder {
        TextView username;
        TextView address;
    }
}
