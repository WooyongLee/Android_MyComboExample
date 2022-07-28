package com.example.mycomboex.ui.chart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.mycomboex.FunctionHandler;
import com.example.mycomboex.MainActivity;
import com.example.mycomboex.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LineChartFunc {
    private ActivityMainBinding binding;
    private MainActivity mActivity;

    private Runnable GateRunnable;
    private Handler GateHandler;
    private int delay = 40000;

    private Boolean isInit = false;

    private float mStartX;
    private float mStopFreq;

    private float MAX_Y = 0;
    private float MIN_Y = -100f;

    private float mTopY = MAX_Y;
    private float mBottomY = MIN_Y;

    public final int GATE_BOX_MAX_COUNT = 8;

    public final int NUMBER_X_GRID_LINE = 11;
    public final int NUMBER_Y_GRID_LINE = 5;

    private ArrayList<Float> mDataList;
    private ArrayList<ILineDataSet> mDataSets;

    // chart object
    private final LineChart lineChart;

    public LineChartFunc(Activity activity, ActivityMainBinding binding)
    {
        mActivity = (MainActivity) activity;
        this.binding = binding;

        mDataSets = new ArrayList<>();

        lineChart = binding.chartLayout.lineChart;

        initValues();
        addEvents();
    }

    public String geStringLineData()
    {
        LineData lineData = lineChart.getLineData();
        String strLineData = "";
        for ( int i = 0 ; i < lineData.getDataSetCount() ; i++)
        {
            strLineData += lineData.getDataSetByIndex(i) + " ";
        }
        return strLineData;
    }

    public LineChart getLineChart()
    {
        return lineChart;
    }

    public void InitChart() throws NullPointerException
    {
        mActivity.requestPermissions();
        mActivity.runOnUiThread(() ->
        {
            // Dataset Clear
            mDataSets.clear();

            lineChart.setRenderer(new LineChartRenderer(lineChart, lineChart.getAnimator(), lineChart.getViewPortHandler()));

            // Get Chart Legend
            Legend legend = lineChart.getLegend();

            legend.setTextColor(Color.WHITE);

            lineChart.setOnChartValueSelectedListener(mActivity);
            lineChart.getAxisLeft().setAxisMaximum(MAX_Y);
            lineChart.getAxisLeft().setAxisMinimum(MIN_Y);
            lineChart.getXAxis().setAxisMinimum(mStartX);
            lineChart.getXAxis().setAxisMaximum(mStopFreq);

            lineChart.getLegend().setEnabled(false);
            lineChart.getDescription().setEnabled(false); //그래프 오른쪽 하단의 설명 유무( 마커랑 관계 X ) // enable description text
            lineChart.setTouchEnabled(true); //false일 경우 화면 터치로 그래프를 움직일 수 없음 ( 마커 찍는 것도 불가능) // enable touch gestures
            lineChart.setDragEnabled(true); // enable scaling and dragging
            lineChart.setScaleEnabled(false);
            lineChart.setDoubleTapToZoomEnabled(false);
            lineChart.setPinchZoom(true); // if disabled, scaling can be done on x- and y-axis separately

            lineChart.post(() -> {
                lineChart.setBackgroundColor(Color.BLACK);
            });

            lineChart.setDrawBorders(true);

            lineChart.getAxisLeft().setDrawAxisLine(false);
            lineChart.setDrawBorders(false);
            lineChart.setBorderColor(Color.WHITE);
            lineChart.setBorderWidth(2);

            lineChart.setViewPortOffsets(80, convertDpToPixel(6, mActivity), 50, convertDpToPixel(4, mActivity));

            // X축 설정
            XAxis xAxis = lineChart.getXAxis();
            xAxis.setTextColor(Color.WHITE);
            xAxis.setAvoidFirstLastClipping(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setEnabled(false);
            xAxis.setDrawGridLines(true);
            xAxis.setGridColor(Color.GRAY);
            xAxis.setGridLineWidth(1f);
            xAxis.setLabelCount(NUMBER_X_GRID_LINE, true);
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return "";
//            }
//        });

            // Y축 설정
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setTextColor(Color.WHITE);
            yAxis.setTextSize(10f);
            yAxis.setLabelCount(NUMBER_Y_GRID_LINE, true);
            yAxis.setDrawGridLines(true);
            yAxis.setGridColor(Color.GRAY);
            yAxis.setGridLineWidth(1f);
            yAxis.setMinWidth(40);
            yAxis.setMaxWidth(40);

//        yAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return "";
//            }
//        });

            this.initDataset();

            // 우측의 y축은 사용 불가하도록 설정
            lineChart.getAxisRight().setEnabled(false);

            lineChart.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            LineData data = createChartData(-100);
            lineChart.setData(data);

            Log.d("LineChartFunc :: ", "lineChart notifyDataSetChanged() !");
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        });
    }

    public void initValues()
    {
        if ( isInit ) {
            return;
        }
        isInit = true;
        mDataList = new ArrayList<>();
        mStartX = 0f;
        mStopFreq = 1000f;

        GateRunnable = () -> {
            // MQTT Init
           // FunctionHandler.getInstance().
        };

        new Handler(Looper.getMainLooper()).post(() ->
        {
           GateHandler = new Handler();
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    public void addEvents()
    {
        lineChart.setOnTouchListener((view, motionEvent) -> {
            float x, y;
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN :
                    Log.d("LineChart MotionEvent :: ", " ACTION_DOWN");
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d("LineChart MotionEvent :: ", " ACTION_MOVE");
                    break;

                case MotionEvent.ACTION_UP:
                    Log.d("LineChart MotionEvent :: ", " ACTION_UP");
                    break;
            }
            return true;
        });
    }

    private LineData createChartData(int range)
    {
        ArrayList<Entry> entry1 = new ArrayList<>();

        LineData chartData = new LineData();

        // Make linear Data
        for (int i = 0 ; i < 1000 ; i++)
        {
            float val1 = i / 10 - 100;
            // Log.d(String.valueOf(i), String.valueOf(val1));
            entry1.add(new Entry(i, val1));
        }

        LineDataSet lineDataSet1 = new LineDataSet(entry1, "Spectrum");
        lineDataSet1.setLineWidth(1);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setDrawCircleHole(false);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setDrawHorizontalHighlightIndicator(false);
        lineDataSet1.setDrawHighlightIndicators(false);
        lineDataSet1.setColor(Color.rgb(255, 155, 155));

        chartData.setValueTextSize(15);
        chartData.addDataSet(lineDataSet1);

        Log.d("createChartData :: ", "Chart Data Generated");

        return chartData;
    }

    private void initDataset()
    {
        createLineDataset();

        createBoxDataSet(mDataSets, Color.BLUE);
        createBoxDataSet(mDataSets, Color.RED);
        createBoxDataSet(mDataSets, Color.BLUE);
        createBoxDataSet(mDataSets, Color.RED);
        createBoxDataSet(mDataSets, Color.BLUE);
        createBoxDataSet(mDataSets, Color.RED);
        createBoxDataSet(mDataSets, Color.BLUE);
        createBoxDataSet(mDataSets, Color.RED);

        for (int i = 0; i < GATE_BOX_MAX_COUNT; ++i) {
            createBorderDataSet(Color.CYAN);
        }
    }

    private void createLineDataset()
    {
        ArrayList<Entry> values = new ArrayList<>();

        LineDataSet d = new LineDataSet(values, "Label");

        d.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.setLineWidth(1f);
        d.setCircleRadius(1f);
        d.setCircleHoleRadius(1f);
        d.setFillAlpha(0);
        d.setValueTextSize(9f);
        d.setHighlightLineWidth(1.5f);

        d.setDrawCircles(false);
        d.setDrawCircles(false);
        d.setDrawValues(false);
        d.setDrawIcons(false);

        int color = Color.YELLOW;

        d.setColor(color);
        d.setCircleColor(color);
        d.setFillColor(color);
        d.setHighLightColor(Color.rgb(244, 117, 117));
        d.setValueTextColor(Color.WHITE);

        mDataSets.add(d);
    }

    private void createBoxDataSet(ArrayList<ILineDataSet> list, int color)
    {
        ArrayList<Entry> values = new ArrayList<>();

        LineDataSet dataSet = new LineDataSet(values, "");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0f);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(125);

        dataSet.setHighlightEnabled(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setLineWidth(0f);
        dataSet.setFormLineWidth(0f);

        dataSet.setValueTextSize(9f);
        dataSet.setHighlightLineWidth(0f);
        dataSet.setDrawValues(false);
        dataSet.setDrawIcons(false);

        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setFillColor(color);
        dataSet.setHighLightColor(color);
        dataSet.setValueTextColor(Color.WHITE);

        list.add(dataSet);
        Log.d("mDataSets", mDataSets.size() + "");
    }

    private void createBorderDataSet(int color)
    {
        ArrayList<Entry> values = new ArrayList<>();

        LineDataSet dataset = new LineDataSet(values, "");

        dataset.setDrawIcons(false);

        dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataset.setDrawFilled(false);
        dataset.setHighlightEnabled(false);

        dataset.setDrawCircles(true);//false);
        dataset.setDrawCircleHole(false);
        dataset.setCircleColor(color);
        dataset.setCircleRadius(4);

        dataset.setValueTextSize(12f);
        dataset.setDrawValues(true);// false);
        dataset.setValueTextColor(color);
        dataset.setValueTypeface(Typeface.DEFAULT_BOLD);

        dataset.setColor(color);

        mDataSets.add(dataset);
    }

    public static float convertDpToPixel(float dp, Context context)
    {
        try {

            float dpi = context.getResources().getDisplayMetrics().densityDpi;
            float density = context.getResources().getDisplayMetrics().density;
            float defaultDensity = DisplayMetrics.DENSITY_DEFAULT;
            float resolutionHeight = context.getResources().getDisplayMetrics().heightPixels;

//        InitActivity.logMsg("dpi/density", dpi  + " " + density);

            float resolutionPerDpi = resolutionHeight / dpi;
//        InitActivity.logMsg("ResolutionPerDpi", resolutionPerDpi + "");

            if (resolutionPerDpi > 3.9f) {

                dpi *= 1.5f;

            } else if (resolutionPerDpi < 2.6f) {

                dpi /= 1.5f;

            }

            float px = dp * (dpi / defaultDensity);

            return px;

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return 0;
    }

}
