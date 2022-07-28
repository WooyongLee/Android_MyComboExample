package com.example.mycomboex;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.mycomboex.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private final String LINE_CHART_KEY = "LINE_CHART";
    private final String LINE_CHART_DATA_KEY = "LINE_CHART_DATA";

    private ActivityMainBinding binding;

    private static final int MULTIPLE_PERMISSION = 10235;

    // 권한 정의
    private String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    // 시간 출력 스레드
    private Thread mTimeThread;
    
    // Chart 도시 스레드
    private Thread mChartThread;

    // 화면 변경 및 종료후 재로딩 카운트
    private static int reLoadCount = 0;

    // 핸들러
    private FunctionHandler mFunctionHandler = null;

    // TAB ACTIVITY 전환 간에 사용하는 요청 코드
    private final int TAB_ACTIVITY_REQUEST_CODE = 1;

    public MainActivity()
    {
        Log.d("MainActivity Constructor :: ", "Create MainActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FloatingActionButton fab = binding.fab;

        binding.timeTextView.setTextColor(Color.BLACK);

        // Chart View Button Click Listner 구현
        binding.tapTestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, TabActivity.class);
                in.putExtra("CURRENT_TIME", binding.timeTextView.getText());

                // TAB Activity로 전환
                startActivityForResult(in, TAB_ACTIVITY_REQUEST_CODE);
                Log.d("MainActivity", "startActivity(intent)");
            }
        });

        binding.wifiButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent in = new Intent(MainActivity.this, WifiActivity.class);
                startActivity(in);
            }
        });


        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Init Handler
        mFunctionHandler = FunctionHandler.getInstance(this, binding);

        // 시간 표시 쓰레드 시작
        mTimeThread = new Thread(new NewRunnable());
        mTimeThread.start();
        Log.d("onCreate() :: ", "Time Thread Start");

        // 별도 쓰레드에서 Chart 설정
        mChartThread = new Thread(() ->
        {
            try
            {
                mFunctionHandler.getLineChartFunc().InitChart();
                Log.d("FunctionHandler.getLineChartFunc() :: ", "InitChart");
            }

            catch ( NullPointerException ex)
            {
                ex.printStackTrace();
            }
        });
        mChartThread.start();

        // 최초 시작을 처리함
        if ( savedInstanceState == null)
        {

        }

        // MainActivity 화면 전환 후
        else
        {
            reLoadCount += 1;
            Log.d("onCreate() ::", "savedInstanceState Reloaded " + String.valueOf(reLoadCount));

            mFunctionHandler.getLineChartFunc().InitChart();
            // binding.chartLayout.lineChart.setNoDataText("I want Display Chart!!!!!!!");
        }

        // 권한 확인
        if ( !hasPermissions(this, PERMISSIONS))
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MULTIPLE_PERMISSION);
        }
        else
        {
            // 권한이 있는 경우에 대해서 처리할 것
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume() :: ", "onResume Call!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult() :: ", "Receive requestCode is " + String.valueOf(requestCode));

        if ( requestCode == TAB_ACTIVITY_REQUEST_CODE)
        {
            // 시간 표시 쓰레드 종료 시 재시작
            if (!mTimeThread.isAlive())
            {
                mTimeThread = new Thread(new NewRunnable());
                mTimeThread.start();
                Log.d("onActivityResult() :: ", "Time Thread ReStart");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // 구성요소들 모두 Put
        super.onSaveInstanceState(outState);
//        Gson gson = new Gson();
//         String json = gson.toJson(mFunctionHandler.getLineChartFunc().geStringLineData());
        // String json = mFunctionHandler.getLineChartFunc().geStringLineData();
        // Log.d("onSaveInstanceState :: ", json);
        // outState.putString(LINE_CHART_KEY, json);
        // outState.putString(LINE_CHART_DATA_KEY, json);

        // Terminate Threads
        mTimeThread.interrupt();

        if ( !mChartThread.isInterrupted())
        {
            mChartThread.interrupt();
        }

        Log.d("onSaveInstanceState() :: ", "Time Thread Paused");
    }

    private static boolean hasPermissions(Context context, String... permissions)
    {
        if (context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if ( ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MULTIPLE_PERMISSION :
                if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // if Has Permission
                }

                // One of all Denied
                else
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("앱 권한");
                    alertDialog.setMessage("권한 설정 필요, 모든 권한을 허용해주길 바람");
                    alertDialog.setPositiveButton("권한설정",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });
                            // 취소
                    alertDialog.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                return;
        }
    }

    public void requestPermissions()
    {

    }

    // OnChartValueSelectedListener Interface 구현부
    @Override
    public void onValueSelected(Entry e, Highlight h)
    {
        
    }

    @Override
    public void onNothingSelected()
    {

    }

    class NewRunnable implements Runnable
    {
        @Override
        public void run() {
            while ( !Thread.currentThread().isInterrupted() )
            {
                try
                {
                    Thread.sleep(100);
                }
                catch ( Exception e)
                {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                // Log.d("Thread Running...", "");
                String strClockText = FunctionHandler.getInstance().setClockTextFunc(new Message());
                binding.timeTextView.setText(strClockText);
            }
        }
    }


}