package com.example.tyfw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.tyfw.api.APICallers;
import com.example.tyfw.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AiPredictionActivity extends AppCompatActivity {
    private TextView seekBarTitleAgg;
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
        SeekBar riskBarAgg = findViewById(R.id.seekBarAgg);
        description = findViewById(R.id.ai_description);
        seekBarTitle = findViewById(R.id.seekBarTitle);
        seekBarTitleAgg = findViewById(R.id.seekBarTitleAgg);
        aiResults = findViewById(R.id.ai_results);

        App config = (App) getApplicationContext();
        int riskTol = config.getRiskTolerance();
        seekBarTitle.setText("Current risk tolerance: " + riskTol);
        riskBar.setProgress(riskTol);
        int riskAgg = config.getRiskAgg();
        seekBarTitleAgg.setText("Current investment aggressiveness: " + riskAgg);
        riskBarAgg.setProgress(riskAgg);

        List<String> responseList = getPrediction();
        String recommendation = (responseList.get(0).equals("false")) ? "hold" : "sell";
        aiResults.setText("Today's ETH value: " + responseList.get(1) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(2) +  " USD\n" + "Our recommendation: " + recommendation + "\n");

        // Helpful/taken from: https://stackoverflow.com/questions/8629535/implementing-a-slider-seekbar-in-android
        riskBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTitle.setText("Current risk tolerance: " + progress + "%");
                App config = (App) getApplicationContext();
                config.setRiskTolerance(progress);

                List<String> responseList = getPrediction();
                if (responseList.get(0).equals(("true")) ) {
                    aiResults.setText("Today's ETH value: " + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(3) +  " USD\n" + "Our recommendation: " + "sell ETH" + "\n");
                } else if (responseList.get(1).equals(("true"))) {
                    aiResults.setText("Today's ETH value: " + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(3) +  " USD\n" + "Our recommendation: " + "buy ETH" + "\n");
                } else {
                    aiResults.setText("Today's ETH value: " + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(3) +  " USD\n" + "Our recommendation: " + "hold ETH" + "\n");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        riskBarAgg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTitleAgg.setText("Current investment aggressiveness: " + progress + "%");
                App config = (App) getApplicationContext();
                config.setRiskAgg(progress);

                List<String> responseList = getPrediction();
                Log.e("a", responseList.toString());
                if (responseList.get(0).equals(("true")) ) {
                    aiResults.setText("Today's ETH value: " + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(3) +  " USD\n" + "Our recommendation: " + "sell ETH" + "\n");
                } else if (responseList.get(1).equals(("true"))) {
                    aiResults.setText("Today's ETH value: " + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(3) +  " USD\n" + "Our recommendation: " + "buy ETH" + "\n");
                } else {
                    aiResults.setText("Today's ETH value: " + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value: " + responseList.get(3) +  " USD\n" + "Our recommendation: " + "hold ETH" + "\n");
                }
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

        APICallers.GetPrediction getPrediction = new APICallers.GetPrediction(config.getRiskTolerance(), config.getRiskAgg(), config.getEmail());
        Thread getPredictionThread = new Thread(getPrediction);
        getPredictionThread.start();
        try {
            getPredictionThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject serverResponse = getPrediction.getValue();
        try {
            Log.e("A", serverResponse.toString());
            List<String> returnList = new ArrayList<>();
            returnList.add(serverResponse.getString("predictBuy"));
            returnList.add(serverResponse.getString("predictSell"));
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


}

