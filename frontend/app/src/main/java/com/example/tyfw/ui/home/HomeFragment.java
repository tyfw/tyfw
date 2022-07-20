package com.example.tyfw.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.example.tyfw.App;
import com.example.tyfw.R;
import com.example.tyfw.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
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
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getContext(), okHttpClient);

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

        lineChart.setNoDataText("Loading Wallet Data");
        // https://stackoverflow.com/questions/30892275/mpandroidchart-change-message-no-chart-data-available
        Paint p = lineChart.getPaint(Chart.PAINT_INFO);
        p.setTextSize(64);

        try {
            setTimeOptions();
            // setChart();
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

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken",  config.getGoogleIdToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetUser getUser = new GetUser(jsonObject);
        Thread getUserThread = new Thread(getUser);
        getUserThread.start();
        try {
            getUserThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject serverResponse = getUser.getValue();
        Log.d(TAG, serverResponse.toString());
        JSONObject user = null;
        try {
            user = serverResponse.getJSONObject("data");
            currUser.setText(user.getString("username"));
            JSONArray addr = user.getJSONArray("addresses");
            currWallet.setText(addr.get(0).toString());

            config.setUsername(user.getString("username"));
        } catch (JSONException e) {
            currUser.setText("null");
            currWallet.setText("null");
            Toast.makeText(getContext(), "You are being rate limited, please reload and try again.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void setBalance(){
        App config = (App) getActivity().getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken",  config.getGoogleIdToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GetBalance getBalance = new GetBalance(jsonObject);
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

        }
    }

    private void setChartGraphics(){
        // background color
        lineChart.setBackgroundColor(Color.WHITE);

        // disable description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        LimitLine ll1 = new LimitLine(30f,"Title");
        ll1.setLineColor(getResources().getColor(R.color.rosy_brown));
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(35f, "");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
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

        lineChart.setData(res);
        lineChart.setVisibleXRange(res.getXMin(), res.getXMax());
        // Im goated for this
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
            case "":
                timeScale = "";
                break;
        }
        Log.d("DATA", timeScale);

        App config = (App) getActivity().getApplicationContext();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", config.getEmail());
            jsonObject.put("googleIdToken",  config.getGoogleIdToken());
            jsonObject.put("time", timeScale);
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
            Toast.makeText(getContext(), "Unable to load data, you might be rate limited.", Toast.LENGTH_SHORT).show();
        }
        return false;
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
                        .addHeaders("time", jsonObject.getString("time"))
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
            //TODO: @Dryden this is where I put in the empty array FE handling
            value = new JSONObject();
            try {
                value.putOpt("data", new JSONArray());
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            //Toast.makeText(getContext(), "Unable to load data for this time option. Please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        public JSONObject getValue() {
            return value;
        }
    }

    class GetUser implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private JSONObject jsonObject;
        private String url = "http://34.105.106.85:8081/user/getuser/";

        public GetUser(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
                        .build();

                ANResponse response = request.executeForJSONObject();

                if (response.isSuccess()) {
                    value = (JSONObject) response.getResult();
                } else {
                    // handle error
                    ANError error = response.getError();
                    int errorCode = error.getErrorCode();
                    if (errorCode == 400) {
                        Toast.makeText(getContext(), "Unable to get all user details from server", Toast.LENGTH_SHORT).show();
                    }
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

    class GetBalance implements Runnable {
        final static String TAG = "GetUserRunnable";
        private JSONObject value;
        private JSONObject jsonObject;
        private String url = "http://34.105.106.85:8081/user/getbalance/";

        public GetBalance(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public void run() {
            try {
                ANRequest request = AndroidNetworking.get(url)
                        .addHeaders("email", jsonObject.getString("email"))
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