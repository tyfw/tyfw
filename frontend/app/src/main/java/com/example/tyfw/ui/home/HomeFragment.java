package com.example.tyfw.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.databinding.FragmentHomeBinding;
import com.example.tyfw.ui.leaderboard.LeaderboardFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

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
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       return root;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        lineChart = view.findViewById(R.id.home_chart);
        dropdown = view.findViewById(R.id.home_graph_options);
        currVal = view.findViewById(R.id.currentvalue);
        currUser = view.findViewById(R.id.user);
        currWallet = view.findViewById(R.id.wallet);

        setChart();
        setTimeOptions();
        currVal.setText("69");
        try{
            App config = (App) getActivity().getApplicationContext();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", config.getEmail());
                jsonObject.put("googleIdToken",  config.getGoogleIdToken());
                jsonObject.put("numPoints", 100);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            GetHome getHome = new GetHome(jsonObject);
            Thread getHomeThread = new Thread(getHome);
            getHomeThread.start();
            try {
                getHomeThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JSONObject serverResponse = getHome.getValue();
            Log.d(TAG, serverResponse.toString());
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

        }
    }

    // Followed this tutorial: https://www.youtube.com/watch?v=TNeE9DJoOMY&list=PLgCYzUzKIBE9Z0x8zVUunk-Flx8r_ioQF&index=6
    // TODO: add custom x-y-margins for graph for each option
    private void setChart(){
        ArrayList<String> xAxis = new ArrayList<>(); // each index contains String of what that is
        ArrayList<Entry> yAxis = new ArrayList<>(); // each index contains data point
        // Assuming xAxis and yAxis are set:
        updateData(xAxis, yAxis);

        ArrayList<ILineDataSet> lineData = new ArrayList<>();

        LineDataSet yData = new LineDataSet(yAxis, "Value");
        yData.setCircleColor(Color.MAGENTA);
        yData.setDrawCircles(true);

        lineData.add(yData);

        String[] xData = xAxis.toArray(new String[0]);

        LineData res = new LineData(lineData);

        lineChart.setData(res);

        lineChart.setVisibleXRange(0,100);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    // Example: https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data
    private void updateData(ArrayList<String> xAxis, ArrayList<Entry> yAxis){
        float startTime = 0;
        float endTime = 100;

        float timeScale = (float) 0.01;

        setCustom(timeOption.equals("Custom"));

        switch (timeOption) {
            case "Daily":
                timeScale = (float) 0.1;
                break;
            case "Weekly":
                timeScale = (float) 1.0;
                break;
            case "Monthly":
                timeScale = (float) 10.0;
                break;
            case "Yearly":
                timeScale = (float) 50.0;
                break;
            case "":
                timeScale = (float) 0.05;
                break;
        }

        for (float t = startTime; t < endTime; t += timeScale) {
            Entry val = new Entry(t, Float.parseFloat(String.valueOf(Math.sin(t))));
            yAxis.add(val);
            xAxis.add(String.valueOf(t));
        }
    }

    private void setCustom(boolean val){
        lineChart.setTouchEnabled(val);
        lineChart.setDragEnabled(val);
        lineChart.setPinchZoom(val);
        lineChart.setScaleEnabled(val);
    }

    class GetHome implements Runnable {
        final static String TAG = "GetHomeRunnable";
        private JSONObject value;
        private JSONObject jsonObject;
        private String url = "http://34.105.106.85:8081/user/displaycurruser/";

        public GetHome(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .addHeaders("googleIdToken", jsonObject.getString("googleIdToken"))
                        .addHeaders("numPoints", Integer.toString(jsonObject.getInt("numPoints")))
                        .setPriority(Priority.MEDIUM)
                        .build();

                ANResponse<JSONObject> response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value =  response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    error.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public JSONObject getValue() {
            return value;
        }
    }

}