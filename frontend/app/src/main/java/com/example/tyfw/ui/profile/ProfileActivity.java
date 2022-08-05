package com.example.tyfw.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyfw.App;
import com.example.tyfw.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.tyfw.api.APICallers.*;

public class ProfileActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Spinner dropdown;
    private String username;
    private static String timeOption = "";

    private final String TAG = "Profile";
    MaterialButton friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView usernameTextView;
        String walletName;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = getIntent().getStringExtra("username");
        walletName = getIntent().getStringExtra("walletAddress");

        usernameTextView = findViewById(R.id.profile_username);
        usernameTextView.setText(username);

        TextView walletAddr = findViewById(R.id.wallet_address);
        walletAddr.setText(walletName);

        ImageView profilePic = (ImageView) findViewById(R.id.profile_default_pic);
        profilePic.setImageResource(R.drawable.ic_baseline_people_24);
        profilePic.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        dropdown = findViewById(R.id.profile_graph_options);
        friend = findViewById(R.id.friend_button);
        lineChart = findViewById(R.id.wallet_chart);

        try {
            setTimeOptions();
            // setChart();
        } catch (Exception e){
            Log.d(TAG,e.toString());
        }

        friend.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                App config = (App) getApplicationContext();
                if (isChecked){

                    AddFriend addFriend = new AddFriend(config.getEmail(), config.getGoogleIdToken(), username);
                    Thread addFriendThread = new Thread(addFriend);
                    addFriendThread.start();
                    try {
                        addFriendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Boolean serverResponse = addFriend.success();
                    Log.d(TAG, serverResponse.toString());

                    if (serverResponse){
                        button.setText("Friends");
                    } else {
                        Toast.makeText(getBaseContext(),"Unable to add this user as friend, please try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    button.setText(R.string.add_friend_button);
                    button.toggle();
                    DeleteFriend delFriend = new DeleteFriend(config.getEmail(), username);
                    Thread delFriendThread = new Thread(delFriend);
                    delFriendThread.start();
                    try {
                        delFriendThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    button.setChecked(!delFriend.getValue());
                }
            }
        });

        friend.setChecked(isFriend());
    }

    private boolean isFriend(){
        App config = (App) getApplicationContext();

        GetFriends getFriend = new GetFriends(config.getEmail());
        Thread getFriendThread = new Thread(getFriend);
        getFriendThread.start();
        try {
            getFriendThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getFriend.getValue();
        JSONArray friendsList = new JSONArray();
        try {
            friendsList = serverResponse.getJSONArray("friends");
            for (int i=0; !friendsList.isNull(i); i++) {
                String friendName = friendsList.getString(i);
                if (friendName.equals(username)) {
                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Unable to check if user's friends.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Followed this API guide: https://developer.android.com/guide/topics/ui/controls/spinner.html#Populate
    private void setTimeOptions(){
        dropdown.setOnItemSelectedListener(new TimeSelectListener());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.time_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
    }

    private class TimeSelectListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            timeOption = parent.getItemAtPosition(position).toString();
            setChart();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // nothing
        }
    }

    // Followed this tutorial: https://www.youtube.com/watch?v=TNeE9DJoOMY&list=PLgCYzUzKIBE9Z0x8zVUunk-Flx8r_ioQF&index=6
    private void setChart(){
        ArrayList<String> xData = new ArrayList<>(); // each index contains String of what that is
        ArrayList<Entry> yAxis = new ArrayList<>(); // each index contains data point
        // Assuming xAxis and yAxis are set:
        if (!updateData(xData,yAxis)){
            // Toast.makeText(getBaseContext(),"Unable load data for this option. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        ArrayList<ILineDataSet> lineData = new ArrayList<>();
        LineDataSet yData = new LineDataSet(yAxis, "Value");
        yData.setCircleColor(Color.MAGENTA);

        lineData.add(yData);

        LineData res = new LineData(lineData);
        Log.e("res", lineData.toString());
        lineChart.setData(res);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDrawBorders(true);
        lineChart.notifyDataSetChanged();
        lineChart.animateXY(1000,1000);
        lineChart.fitScreen();

        Description des = new Description();
        des.setText("Value");
        des.setPosition(0,0);
        des.setTextColor(Color.CYAN);
        des.setTextSize(16f);
        lineChart.setDescription(des);

        XAxis x_axis = lineChart.getXAxis();

        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        x_axis.setPosition(position);
    }

    // Example: https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data
    private boolean updateData(ArrayList<String> xAxis, ArrayList<Entry> yAxis){
        String timeScale = "";

        timeScale = getTimeScale();

        Log.d("DATA", timeScale);

        App config = (App) this.getApplicationContext();

        GetProfile getProfile = new GetProfile(config.getEmail(), config.getGoogleIdToken(), timeScale, username);
        Thread getHomeThread = new Thread(getProfile);
        getHomeThread.start();
        try {
            getHomeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getProfile.getValue();
        if (serverResponse == null) {
            Toast.makeText(getBaseContext(),"Unable load user profile, please try again.", Toast.LENGTH_LONG).show();
            return false;
        }
        Log.e(TAG, serverResponse.toString());

        JSONArray data = new JSONArray();
        try {
            data = serverResponse.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (data.length() > 0){
            for (int i =0; i< data.length(); i++) {
                Entry val;
                try {
                    val = new Entry(i,  (float) data.getDouble(i));
                } catch (JSONException e) {
                    val = new Entry();
                    e.printStackTrace();
                }
                yAxis.add(val);
                xAxis.add(String.valueOf(i));
            }
            return true;
        }
        return false;
    }

    private String getTimeScale(){
        switch (timeOption) {
            case "Today":
                return "day";
            case "Last Week":
                return "week";
            case "Last Month":
                return "month";
            case "Last Year":
                return "year";
            default:
                return null;
        }
    }






}