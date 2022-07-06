package com.example.tyfw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tyfw.ui.profile.ProfileActivity;
import com.example.tyfw.ui.profile.WalletProfileActivity;
import com.example.tyfw.utils.LeaderboardListAdapter;
import com.example.tyfw.utils.LeaderboardRow;
import com.example.tyfw.utils.SearchResultsListAdapter;
import com.example.tyfw.utils.SearchResultsRow;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    final static String TAG = "SearchResultsActivity";

    private List<SearchResultsRow> itemsList = new ArrayList<SearchResultsRow>();
    private ListView listView;
    private SearchResultsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        // TODO: call the api with the search string
        // Should be passed as extra args to this

        listView = (ListView) findViewById(R.id.search_results_list);
        adapter = new SearchResultsListAdapter(this, itemsList);
        listView.setAdapter(adapter);

        for (int i = 0; i < 10; i++) {
            SearchResultsRow items = new SearchResultsRow();

            items.setValue(Integer.toString(i));

            itemsList.add(items);
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();

        // Followed this SOF post: https://stackoverflow.com/questions/32827787/intent-in-listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = adapterView.getItemAtPosition(i).toString();

                Intent intent;
                if (isProfile(item)){
                    intent = new Intent(SearchResultsActivity.this, ProfileActivity.class);
                    intent.putExtra("username", item);
                } else {
                    intent = new Intent(SearchResultsActivity.this, WalletProfileActivity.class);
                    intent.putExtra("walletAddress", item);
                }
                startActivity(intent);

            }

            // TODO: make this a valid profile checker
            private boolean isProfile(@NonNull String s){
                return s.length()<5;
            }
        });
    }


}
