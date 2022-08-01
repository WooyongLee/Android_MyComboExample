package com.example.mycomboex.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mycomboex.RemoteStub;

// 원격 서비스의 생명주기 구현 클래스
public class MyService extends Service {

    RemoteStub.Stub mBinder = new RemoteStub.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String getServicePackageName() throws RemoteException {
            return MyService.this.getPackageName();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        Toast.makeText(this, "[RemoteService] onBind() Call", Toast.LENGTH_SHORT).show();
        Log.d("[RemoteService]", "onBind() Call");

        // Skeleton 객체 반환
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Toast.makeText(this, "[RemoteService] onCreate() Call", Toast.LENGTH_SHORT).show();
        Log.d("[RemoteService]", "onCreate() Call");
    }

    public void onDestroy()
    {
        Toast.makeText(this, "[RemoteService] onDestroy() Call", Toast.LENGTH_SHORT).show();
        Log.d("[RemoteService]", "onDestroy() Call");

        super.onDestroy();
    }
}
