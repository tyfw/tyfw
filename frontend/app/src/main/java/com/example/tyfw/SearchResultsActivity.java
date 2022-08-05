package com.example.tyfw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tyfw.ui.profile.ProfileActivity;
import com.example.tyfw.utils.SearchResultsListAdapter;
import com.example.tyfw.utils.SearchResultsRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private final List<SearchResultsRow> itemsList = new ArrayList<SearchResultsRow>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        // TODO: call the api with the search string
        // Should be passed as extra args to this

        ListView listView = (ListView) findViewById(R.id.search_results_list);
        SearchResultsListAdapter adapter = new SearchResultsListAdapter(this, itemsList);
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("serverResponse");
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonArray = jsonObject.getJSONArray("queryMatches");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray == null) {
            try {
                jsonArray = new JSONArray("[]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SearchResultsRow firstSearchResultsRow = new SearchResultsRow();
        firstSearchResultsRow.setUsername("Username");
        firstSearchResultsRow.setWallet("Wallet address");
        itemsList.add(firstSearchResultsRow);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < jsonArray.length(); i++) {
            SearchResultsRow items = new SearchResultsRow();
            try {
                items.setUsername(jsonArray.getJSONObject(i).getString("username"));
                items.setWallet(jsonArray.getJSONObject(i).getJSONArray("addresses").getString(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            itemsList.add(items);
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();

        // Followed this SOF post: https://stackoverflow.com/questions/32827787/intent-in-listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i!=0){
                    SearchResultsRow item;

                    item = (SearchResultsRow) adapterView.getItemAtPosition(i);

                    Intent intent;

                    intent = new Intent(SearchResultsActivity.this, ProfileActivity.class);
                    intent.putExtra("username", item.getUsername());
                    intent.putExtra("walletAddress", item.getWallet());
                    startActivity(intent);
                }
            }
        });
    }


}
