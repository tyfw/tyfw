package com.example.tyfw.ui.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.ui.home.HomeFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
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


public class ProfileActivity extends AppCompatActivity {

    private LineChart lineChart;
    private Spinner dropdown;
    private String username;
    private static String timeOption = "";

    private final String TAG = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = getIntent().getStringExtra("username");
        String walletName = getIntent().getStringExtra("walletAddress");

        TextView usernameTextView = findViewById(R.id.profile_username);
        usernameTextView.setText(username);

        TextView walletAddr = findViewById(R.id.wallet_address);
        walletAddr.setText(walletName);

        ImageView profilePic = (ImageView) findViewById(R.id.profile_default_pic);
        profilePic.setImageResource(R.drawable.ic_baseline_people_24);
        profilePic.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        dropdown = findViewById(R.id.profile_graph_options);
        MaterialButton friend = findViewById(R.id.friend_button);

        // THIS WAS CAUSING THE CRASHES
        lineChart = findViewById(R.id.wallet_chart);

        try {
            setTimeOptions();
            // setChart();
        } catch (Exception e){
            Log.d(TAG,e.toString());
        }

        friend.addOnCheckedChangeListener(new MaterialButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(MaterialButton button, boolean isChecked) {
                App config = (App) getApplicationContext();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", config.getEmail());
                    jsonObject.put("googleIdToken",  config.getGoogleIdToken());
                    jsonObject.put("friendUsername", username);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AddFriend addFriend = new AddFriend(jsonObject);
                Thread addFriendThread = new Thread(addFriend);
                addFriendThread.start();
                try {
                    addFriendThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                boolean serverResponse = addFriend.success();
                Log.d(TAG, Boolean.toString(serverResponse));

                if (isChecked){
                    if (serverResponse){
                        button.setText("Friends");
                    } else {
                        Toast.makeText(getBaseContext(),"Unable to add this user as friend, please try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    button.setText(R.string.add_friend_button);
                }
            }
        });
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
            parent.setSelection(0);
        }
    }

    // Followed this tutorial: https://www.youtube.com/watch?v=TNeE9DJoOMY&list=PLgCYzUzKIBE9Z0x8zVUunk-Flx8r_ioQF&index=6
    // TODO: add custom x-y-margins for graph for each option
    private void setChart(){
        ArrayList<String> xData = new ArrayList<>(); // each index contains String of what that is
        ArrayList<Entry> yAxis = new ArrayList<>(); // each index contains data point
        // Assuming xAxis and yAxis are set:
        //TODO: make a better way of reloading
        updateData(xData,yAxis);


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
    private void updateData(ArrayList<String> xAxis, ArrayList<Entry> yAxis){
        String timeScale = "";
        switch (timeOption) {
            case "Today":
                timeScale = "day";
                break;
            case "Last Week":
                timeScale = "week";
                break;
            case "Last Month":
                timeScale = "month";
                break;
            case "Last Year":
                timeScale = "year";
                break;
            default:
                timeScale = "";
                break;
        }
        Log.d("DATA", timeScale);

        App config = (App) this.getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken",  config.getGoogleIdToken());
            jsonObject.put("time", timeScale);
            jsonObject.put("friendUsername", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetProfile getProfile = new GetProfile(jsonObject);
        Thread getHomeThread = new Thread(getProfile);
        getHomeThread.start();
        try {
            getHomeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getProfile.getValue();
        Log.e(TAG, serverResponse.toString());

        JSONArray data = new JSONArray();
        try {
            data = serverResponse.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
    }


    class GetProfile implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetProfile(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/displayotheruserbyusername/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("otherUsername", jsonObject.getString("friendUsername"))
                        .addHeaders("time", jsonObject.getString("time"))
                        .setPriority(Priority.MEDIUM)
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    error.printStackTrace();
                    errorResponse(error);
                }
            } catch (JSONException e) {
                errorResponse(e);
            }
        }

        private void errorResponse(Exception e){
            value = new JSONObject();
            try {
                value.putOpt("data", new JSONArray());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        public JSONObject getValue() {
            return value;
        }
    }

    static class AddFriend implements Runnable {
        final static String TAG = "GetProfileRunnable";
        private boolean value;
        private final JSONObject jsonObject;

        public AddFriend(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            String url = "http://34.105.106.85:8081/user/addbyusername/";
            ANRequest request = AndroidNetworking.post(url)
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse response = request.executeForOkHttpResponse();

            if (response.isSuccess()) {
                value = response.getOkHttpResponse().isSuccessful();
            } else {
                // handle error
                ANError error = response.getError();
                error.printStackTrace();
            }
        }

        public boolean success() {
            return value;
        }
    }
}