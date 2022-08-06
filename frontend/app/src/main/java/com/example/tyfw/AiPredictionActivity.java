package com.example.tyfw;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.tyfw.api.APICallers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class AiPredictionActivity extends AppCompatActivity {
    private TextView seekBarTitleAgg;
    private TextView seekBarTitle;
    private TextView aiResults;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_prediction);

        seekBarTitle = findViewById(R.id.seekBarTitle);
        SeekBar riskBar = findViewById(R.id.seekBar);
        SeekBar riskBarAgg = findViewById(R.id.seekBarAgg);
        seekBarTitle = findViewById(R.id.seekBarTitle);
        seekBarTitleAgg = findViewById(R.id.seekBarTitleAgg);
        aiResults = findViewById(R.id.ai_results);

        Button riskButton = findViewById(R.id.risk_button);

        App config = (App) getApplicationContext();
        int riskTol = config.getRiskTolerance();
        seekBarTitle.setText("Current risk tolerance:\n" + riskTol);
        riskBar.setProgress(riskTol);
        int riskAgg = config.getRiskAgg();
        seekBarTitleAgg.setText("Current investment aggressiveness:\n" + riskAgg);
        riskBarAgg.setProgress(riskAgg);

        List<String> responseList = getPrediction();
        showPredictions(responseList);

        // Helpful/taken from: https://stackoverflow.com/questions/8629535/implementing-a-slider-seekbar-in-android
        riskBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTitle.setText("Current risk tolerance:\n" + progress + "%");
                App config = (App) getApplicationContext();
                config.setRiskTolerance(progress);

                List<String> responseList = getPrediction();
                showPredictions(responseList);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // ignore
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // ignore
            }
        });

        riskBarAgg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarTitleAgg.setText("Current investment aggressiveness:\n" + progress + "%");
                App config = (App) getApplicationContext();
                config.setRiskAgg(progress);

                List<String> responseList = getPrediction();
                showPredictions(responseList);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // ignore
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // ignore
            }
        });

        riskButton.setOnClickListener(v -> {
            new SimpleTooltip.Builder(getApplicationContext())
                    .anchorView(seekBarTitleAgg)
                    .text("Lower investment aggressiveness: more likely to buy when the predicted price is larger than today's price.")
                    .gravity(Gravity.BOTTOM)
                    .animated(true)
                    .transparentOverlay(false)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR_ROUNDED)
                    .cornerRadius(20)
                    .overlayOffset(0)
                    .build()
                    .show();

            new SimpleTooltip.Builder(getApplicationContext())
                    .anchorView(seekBarTitle)
                    .text("Higher risk tolerance: less likely to sell on a low predicted price.")
                    .gravity(Gravity.TOP)
                    .animated(true)
                    .transparentOverlay(false)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR_ROUNDED)
                    .cornerRadius(20)
                    .overlayOffset(0)
                    .build()
                    .show();
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
            returnList.add("Error, unable to access server");
            return returnList;
        }
    }

    @SuppressLint("SetTextI18n")
    private void showPredictions(List<String> responseList) {
        if (responseList.get(0).equals(("1")) ) {
            aiResults.setText("Today's ETH value:\n" + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value:\n" + responseList.get(3) +  " USD\n" + "Our recommendation:\n" + "buy ETH" + "\n");
        } else if (responseList.get(1).equals(("1"))) {
            aiResults.setText("Today's ETH value:\n" + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value:\n" + responseList.get(3) +  " USD\n" + "Our recommendation:\n" + "sell ETH" + "\n");
        } else {
            aiResults.setText("Today's ETH value:\n" + responseList.get(2) + " USD\n" + "Tomorrow's predicted ETH value:\n" + responseList.get(3) +  " USD\n" + "Our recommendation:\n" + "hold ETH" + "\n");
        }
    }


}

