package com.example.tyfw.ui.graph;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;

public class graphUi {

    public static void nightModeUI(LineChart lineChart, Context c){
        LineData data = lineChart.getData();

        XAxis xAxis = lineChart.getXAxis();

        int nightModeFlags =
                c.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            default:
            case Configuration.UI_MODE_NIGHT_YES:
                lineChart.setBorderColor(Color.WHITE);
                data.setValueTextColor(Color.WHITE);
                xAxis.setTextColor(Color.WHITE);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                data.setValueTextColor(Color.BLACK);
                break;

        }
    }
}
