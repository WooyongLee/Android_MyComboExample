package com.example.mycomboex;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycomboex.databinding.WifiLayoutBinding;
import com.example.mycomboex.ui.adapter.WifiAdapter;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class WifiActivity extends AppCompatActivity implements AutoPermissionsListener
{
    private static WifiActivity mActivity;

    // App Environment에 대한 Interface, Abtraction Class - App의 Current State
    // App에 Approach 및 Level Task를 Call
    private Context mContext;

    private IntentFilter intentFilter = new IntentFilter();

    private WifiLayoutBinding binding;

    private WifiManager wifiManager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public WifiActivity()
    {
        mActivity = this;
    }

    private void scanSuccess()
    {
        List<ScanResult> results = wifiManager.getScanResults();
        mAdapter = new WifiAdapter(results);
        recyclerView.setAdapter(mAdapter);

        Log.d("wifi", "scan success");
        StringBuffer st = new StringBuffer();
        for (ScanResult r : results)
        {
            Log.d("wifi",""+r);
            st.append(r.SSID);
            st.append("\n");
        }
    }

    private void scanFailure()
    {
        // handle failure: new scan did NOT succeed
        Log.d("wifi", "scanFailure");
        Toast.makeText(mContext,"wifi scan에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        // potentially use older scan results ...
    }

    public void wifiScan()
    {
        wifiManager = (WifiManager)mContext.getSystemService(WIFI_SERVICE);

        // 주기적으로 Wifi 신호를 받아오는 BroadcastReceiver
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent)
            {
                final String action = intent.getAction();
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                {

                }

                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success)
                {
                    scanSuccess();
                }
                else
                {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mContext = this;
        // Event Bus..?
        try
        {
            EventBus.getDefault().register(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        binding = WifiLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recycleListView;

        // get access to location permission
        if (Build.VERSION.SDK_INT >= 23)
        {
            // 권한 허용 여부를 체크함
            if (ActivityCompat.checkSelfPermission(WifiActivity.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                Log.d("requestPermissions()", "Set Permission");

                // 권한을 설정함 -> 설정 후 결과는 onRequestPermissionsResult() 에서 확인할 수 있음
                ActivityCompat.requestPermissions(WifiActivity.getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE_ASK_PERMISSIONS);

                // 권한 요청을 실행한 저 있고, 사용자가 이를 거절했을 때 true 반환
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        binding.reLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                wifiScan();
            }
        });
    }

    @Override
    public void onDenied(int i, @NonNull String[] strings)
    {
        Toast.makeText(this, "onDenied~~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, @NonNull String[] strings)
    {
        Toast.makeText(this, "onGranted~~", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    wifiScan();
                    Log.d("wifi", "In this");

                }
                else {
                    // Permission Denied
                    Log.d("wifi", "permission denied");
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    public static Activity getActivity()
    {
        return mActivity;
    }
}
