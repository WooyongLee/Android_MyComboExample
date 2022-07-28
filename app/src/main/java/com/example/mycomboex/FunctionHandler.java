package com.example.mycomboex;

import android.os.Message;
import android.util.Log;

import com.example.mycomboex.databinding.ActivityMainBinding;
import com.example.mycomboex.ui.chart.LineChartFunc;
import com.github.mikephil.charting.charts.LineChart;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FunctionHandler {
    private static FunctionHandler mHandler = null;

    private MainActivity mActivity;
    private ActivityMainBinding binding;

    private LineChartFunc lineChartFunc;

    public FunctionHandler(MainActivity activity, ActivityMainBinding binding) {
        mActivity = activity;
        this.binding = binding;
    }

    public static FunctionHandler getInstance() {
        return mHandler;
    }

    public static FunctionHandler getInstance(MainActivity activity, ActivityMainBinding binding) {
        if (mHandler == null)
            mHandler = new FunctionHandler(activity, binding);
        return mHandler;
    }


    public LineChartFunc getLineChartFunc()
    {
        if ( lineChartFunc == null )
        {
            lineChartFunc = new LineChartFunc(mActivity, binding);
        }
        else
        {
            Log.d("getLineChartFunc() :: ", "lineChartFunc is Not Null!!");
        }
        return lineChartFunc;
    }

    public String setClockTextFunc(Message msg)
    {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strTime = sdf.format(cal.getTime());

        // binding.timeTextView.setText(strTime);  -> TextView에 나타나지 않음
        // Log.d("Current Time :", strTime);
        return strTime;
    }
}
