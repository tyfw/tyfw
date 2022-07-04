package com.example.tyfw.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tyfw.R;
import com.example.tyfw.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private LineChart lineChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       return root;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        setChart(view);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Followed this tutorial: https://www.youtube.com/watch?v=TNeE9DJoOMY&list=PLgCYzUzKIBE9Z0x8zVUunk-Flx8r_ioQF&index=6
    private void setChart(View view){
        lineChart = view.findViewById(R.id.home_chart);

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

        lineChart.invalidate();
    }

    // Example: https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data
    private void updateData(ArrayList<String> xAxis, ArrayList<Entry> yAxis){
        float startTime = 0;
        float endTime = 100;
        float timeScale = (float) 0.1;

        for (float t = startTime; t<endTime; t+=timeScale){
            Entry val = new Entry(t, Float.parseFloat(String.valueOf(Math.sin(t))));
            yAxis.add(val);
            xAxis.add(String.valueOf(t));
        }
    }


}