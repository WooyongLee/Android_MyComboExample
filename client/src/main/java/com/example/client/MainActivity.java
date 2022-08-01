package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycomboex.RemoteStub;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RemoteStub mBinder = null;
    private TextView text;

    ServiceConnection srvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 원격 서비스 연결 성공
            mBinder = RemoteStub.Stub.asInterface(iBinder);
            Log.d("onServiceConnected()", "Remote Service COnnection Success!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("onServiceDisconnected()", "Remote Service DisConnected!");
            // 원격 서비스 연결 종료
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.packagenametxt);

        Button startButton = (Button)this.findViewById(R.id.startbutton);
        startButton.setOnClickListener(this);

        Button getButton = (Button)this.findViewById(R.id.getbutton);
        getButton.setOnClickListener(this);

        Button stopButton = (Button)this.findViewById(R.id.stopbutton);
        stopButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.startbutton:
                Toast.makeText(this, "원격 서비스 실행", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Execute Start Service");

                Intent intent = new Intent();
                ComponentName name = new ComponentName(
                        "com.example.mycomboex.service", // 패키지명
                        "com.example.mycomboex.service.MyService" // 원격 서비스 클래스명
                );
                intent.setComponent(name);
                bindService(intent, srvConn, Context.BIND_AUTO_CREATE);
                break;

            case R.id.getbutton:
                Toast.makeText(this, "패키지명 가져오기", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Load Package Name");
                
                // Package 명 얻어옴
                String packageName = null;
                try
                {
                    packageName = mBinder.getServicePackageName();
                }
                catch (RemoteException e)
                {
                    
                }
                
                // TextView에 원격 서비스의 패키지명 출력
                text.setText(packageName);
                break;

            case R.id.stopbutton:
                Toast.makeText(this, "원격 서비스 종료", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "Remote Service Exit!");

                this.unbindService(srvConn);
                break;
        }
    }
}