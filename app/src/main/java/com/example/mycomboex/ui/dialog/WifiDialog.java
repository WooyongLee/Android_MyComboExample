package com.example.mycomboex.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycomboex.R;
import com.example.mycomboex.databinding.WifiLayoutBinding;

import org.greenrobot.eventbus.EventBus;

public class WifiDialog
{
    private Context context;
    private EditText message;
    private TextView title;
    private Button okButton;
    private Button cancelButton;

    private WifiLayoutBinding binding;

    public WifiDialog(Context mContext)
    {
        this.context = mContext;
    }

    // Dialog Function
    @SuppressLint("ResourceType")
    public void callFunction(final String ssid)
    {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dlg.setContentView(R.xml.enter_pw_dialog);
        message = (EditText) dlg.findViewById(R.id.message);
        title = (TextView) dlg.findViewById(R.id.title);
        okButton = (Button) dlg.findViewById(R.id.okButton);
        cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        title.setText(ssid);

        dlg.show();

        final String[] pw = new String[1];
        okButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                pw[0] = message.getText().toString();

                Log.d("wifi", "wifiDialog\npw : " + pw[0]);
                EventBus.getDefault().post(new WifiData(ssid, pw[0]));

                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소하였습니다", Toast.LENGTH_SHORT).show();

                dlg.dismiss();
            }
        });
    }

    // Event Bus에 정의한 WifiData Class
    public class WifiData
    {
        public final String ssid;
        public final String pw;

        public WifiData(String ssid, String s) {
            this.ssid = ssid;
            this.pw = s;
        }
    }
}
