package com.example.tyfw.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.example.tyfw.ui.ai.AiPredictionActivity;
import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.api.APICallers;
import com.example.tyfw.databinding.FragmentHomeBinding;
import com.example.tyfw.ui.graph.GraphUiUtils;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private static String timeOption = "";

    private LineChart lineChart;
    private Spinner dropdown;
    private TextView currVal;
    private TextView currWallet;
    private TextView currUser;

    private final String TAG = "HOME";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getContext(), okHttpClient);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        lineChart = view.findViewById(R.id.home_chart);
        dropdown = view.findViewById(R.id.home_graph_options);
        currVal = view.findViewById(R.id.currentvalue);
        currUser = view.findViewById(R.id.user);
        currWallet = view.findViewById(R.id.wallet);

        Button aiButton = view.findViewById(R.id.ai_button);
        aiButton.setOnClickListener(v ->{
            Intent mainActivity = new Intent(getContext(), AiPredictionActivity.class);
            startActivity(mainActivity);
        });

        lineChart.setNoDataText("Loading Wallet Data");
        // https://stackoverflow.com/questions/30892275/mpandroidchart-change-message-no-chart-data-available
        Paint p = lineChart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(64);

        try {
            setTimeOptions();
        } catch (Exception e){
            Log.d(TAG,e.toString());
        }

        setUserData();
        setBalance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // INTERNAL HELPER FUNCTIONS

    private void setUserData(){
        App config = (App) getActivity().getApplicationContext();

        APICallers.GetUser getUser = new APICallers.GetUser(config.getEmail(), config.getGoogleIdToken());
        Thread getUserThread = new Thread(getUser);
        getUserThread.start();
        try {
            getUserThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getUser.getValue();
        JSONObject user = null;
        try {
            user = serverResponse.getJSONObject("data");
            Log.e("SERVER_RESPONSE_userdata", user.toString());
            currUser.setText(user.getString("username"));
            JSONArray addr = user.getJSONArray("addresses");
            currWallet.setText(addr.get(0).toString());
            int riskTolerance = user.getInt("risktolerance");
            config.setRiskTolerance(riskTolerance);
            int riskAgg = user.getInt("riskAgg");
            config.setRiskAgg(riskAgg);
            currWallet.setText(addr.get(0).toString());
            config.setUsername(user.getString("username"));
        } catch (JSONException e) {
            currUser.setText("null");
            currWallet.setText("null");
            Toast.makeText(getContext(), "Unable to fetch user data! Please try to reload this page later.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void setBalance(){
        App config = (App) getActivity().getApplicationContext();

        APICallers.GetBalance getBalance = new APICallers.GetBalance(config.getEmail(), config.getGoogleIdToken());
        Thread getBalanceThread = new Thread(getBalance);
        getBalanceThread.start();
        try {
            getBalanceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getBalance.getValue();
        if (serverResponse == null) {
            Toast.makeText(getContext(), "Unable to get balance, you might be rate limited ", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, serverResponse.toString());
            try {
                DecimalFormat df = new DecimalFormat("0.00");
                currVal.setText(df.format(serverResponse.getDouble("balance"))+ " USD");
            } catch (JSONException e) {
                currVal.setText("?");
                Toast.makeText(getContext(), "You are being rate limited, please reload and try again.",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // Followed this API guide: https://developer.android.com/guide/topics/ui/controls/spinner.html#Populate
    private void setTimeOptions(){
        dropdown.setOnItemSelectedListener(new TimeSelectListener());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),R.array.time_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(adapter);
    }

    private class TimeSelectListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            timeOption = parent.getItemAtPosition(position).toString();
            HomeFragment.this.setChart();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            parent.setSelection(0);   // Enforce a default selection when nothing is selected
        }
    }

    // Followed this tutorial: https://www.youtube.com/watch?v=TNeE9DJoOMY&list=PLgCYzUzKIBE9Z0x8zVUunk-Flx8r_ioQF&index=6
    private void setChart(){
        ArrayList<String> xData = new ArrayList<>(); // each index contains String of what that is
        ArrayList<Entry> yAxis = new ArrayList<>(); // each index contains data point
        updateData(xData,yAxis);

        ArrayList<ILineDataSet> lineData = new ArrayList<>();
        LineDataSet yData = new LineDataSet(yAxis, "Value");
        yData.setCircleColor(Color.DKGRAY);

        lineData.add(yData);

        LineData res = new LineData(lineData);

        lineChart.setData(res);
        lineChart.setVisibleXRange(res.getXMin(), res.getXMax());
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.setDrawBorders(true);
        lineChart.notifyDataSetChanged();
        lineChart.animateXY(1000,1000);
        lineChart.fitScreen();

        GraphUiUtils.nightModeUI(lineChart,getContext());
        Description d = new Description();
        d.setText("");
        lineChart.setDescription(d);    // Hide the description
        lineChart.getAxisRight().setDrawLabels(false);

        lineChart.getLegend().setEnabled(false);   // Hide the legend
    }

    // Example: https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data
    private boolean updateData(ArrayList<String> xAxis, ArrayList<Entry> yAxis){
        String timeScale = "";
        timeScale = getTimeScale();
        Log.d("DATA", timeScale);

        App config = (App) getActivity().getApplicationContext();

        APICallers.GetHome getHome = new APICallers.GetHome(config.getEmail(), timeScale);
        Thread getHomeThread = new Thread(getHome);
        getHomeThread.start();
        try {
            getHomeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getHome.getValue();
        Log.d(TAG, serverResponse.toString());

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
        } else {
            Toast.makeText(getContext(), "Unable to load data. Please try again later.", Toast.LENGTH_SHORT).show();
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