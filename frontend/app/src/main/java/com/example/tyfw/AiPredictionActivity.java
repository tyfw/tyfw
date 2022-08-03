package com.example.tyfw;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AiPredictionActivity extends AppCompatActivity {
    private TextView seekBarTitle;
    private TextView aiResults;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_prediction);

        TextView description = findViewById(R.id.ai_description);
        seekBarTitle = findViewById(R.id.seekBarTitle);
        aiResults = findViewById(R.id.ai_results);
        SeekBar riskBar = findViewById(R.id.seekBar);

        App config = (App) getApplicationContext();
        int riskTol = config.getRiskTolerance();
        seekBarTitle.setText("Current risk tolerance: " + riskTol);
        riskBar.setProgress(riskTol);

        List<String> responseList = getPrediction();
        String recommendation = (responseList.get(0).equals("false")) ? "hold" : "sell";
        aiResults.setText("Today's ETH value: " + responseList.get(1) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(2) +  " USD\n" + "Our recommendation: " + recommendation + "\n");

        // Helpful/taken from: https://stackoverflow.com/questions/8629535/implementing-a-slider-seekbar-in-android
        riskBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTitle.setText("Current risk tolerance: " + progress);
                App config = (App) getApplicationContext();
                config.setRiskTolerance(progress);

                List<String> responseList = getPrediction();
                String recommendation = (responseList.get(0).equals("false")) ? "hold" : "sell";
                aiResults.setText("Today's ETH value: " + responseList.get(1) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(2) +  " USD\n" + "Our recommendation: " + recommendation + "\n");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
    private List<String> getPrediction(){
        App config = (App) getApplicationContext();
        int riskTolerance = config.getRiskTolerance();

        JSONObject jsonObject = new JSONObject();
        try {
            Log.e("HERE", String.valueOf(riskTolerance));
            jsonObject.put("risktolerance", riskTolerance);
            jsonObject.put("email", config.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetPrediction getPrediction = new GetPrediction(jsonObject);
        Thread getPredictionThread = new Thread(getPrediction);
        getPredictionThread.start();
        try {
            getPredictionThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject serverResponse = getPrediction.getValue();
        try {
            List<String> returnList = new ArrayList<>();
            returnList.add(serverResponse.getString("prediction"));
            returnList.add(String.valueOf(serverResponse.getDouble("todayPrice")));
            returnList.add(String.valueOf(serverResponse.getDouble("tomorrowPrice")));
            return returnList;
        } catch (JSONException e) {
            e.printStackTrace();
            List<String> returnList = new ArrayList<>();
            returnList.add("Error, unable to access server");
            returnList.add("Error, unable to access server");
            returnList.add("Error, unable to access server");
            return returnList;
        }
    }

    static class GetPrediction implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private final JSONObject jsonObject;

        public GetPrediction(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                String url = "http://34.105.106.85:8081/user/getprediction/";
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("risktolerance", jsonObject.getString("risktolerance"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
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
}

