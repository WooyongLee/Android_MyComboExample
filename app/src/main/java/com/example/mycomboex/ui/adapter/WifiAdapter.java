package com.example.mycomboex.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycomboex.R;
import com.example.mycomboex.ui.dialog.WifiDialog;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;


public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder>
{
    private List<ScanResult> items;
    private Context mContext;
    // private final WeakReference wifiAdapterReference;
    public WifiAdapter(List<ScanResult> items)
    {
        // wifiAdapterReference = new WeakReference(items);
        this.items = items;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater :: XML에 미리 정의해둔 틀을 실제 메모리에 올려주는 역할, XML의 Resource를 View 객체로 반환
        mContext = parent.getContext();
        
        // Layout Inflater 설정
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // RecyclerView Item 을 ViewHolder에 설정
        @SuppressLint("ResourceType") View view = inflater.inflate(R.xml.wifi_recyclerview_item, parent, false);
        WifiAdapter.WifiViewHolder viewHolder = new WifiViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        // ViewHolder를 어떠한 데이터와 연결할 때 호출하는 method, ViewHolder 객체들의 Layout을 채워주는 역할
        // Data 순서에 맞게 Item Layout 을 Binding 할 수 있음
        ScanResult item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // View Holder 생성
    public class WifiViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewWifiName;

        public WifiViewHolder(View itemView)
        {
            super(itemView);
            textViewWifiName = itemView.findViewById(R.id.tv_wifiName);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // 현재 Click Event가 발생된 지점의 Position을 알아냄
                    int pos = getAdapterPosition();
                    if ( pos != RecyclerView.NO_POSITION)
                    {
                        // List 에서 Item 참조
                        String ssid = items.get(pos).SSID;
                         // PW 입력 Dialog 호출
                        WifiDialog customDialog = new WifiDialog(mContext);
                        customDialog.callFunction(ssid);
                    }
                }
            });
        }

        public void setItem(ScanResult scanResult) {
            textViewWifiName.setText(scanResult.SSID);
        }
    }

}
