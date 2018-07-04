package com.reveautomation.revesmartsecuritykit_online.alluiactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GcHubSync extends AppCompatActivity {

    @BindView(R.id.bt_gc_connect)
    Button btGcconnect;
    @BindView(R.id.bt_gc_sync)
    Button btGcsync;
    @BindView(R.id.et_hub_ip)
    EditText etHubip;
    public Socket socket;
    public static String ips = "192.168.1.104";
    public OutputStream or;
    public InputStream inr;
    public static int ports = 7777, status;
    static String buffstring = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gc_hub_sync);
        ButterKnife.bind(this);
    }
    @OnClick(R.id.bt_gc_sync)
    public void onBtGcSyncClick() {
        String data = DashboardActivity.getSInstance().GCSENSORSTR;
       data = data.substring(0,data.length()-1);
       Log.d("Data GC", data);
        send_data(("*"+data+"#").getBytes());
    }
    @OnClick(R.id.bt_gc_connect)
    public void onBtGcConnect() {
        if (!etHubip.getText().equals("") && !etHubip.getText().equals(null)) {
            if (status == 0) {
                try {
                    new Thread(() -> {
                        Log.d("starter", "starting 2");
                        //  Toast.makeText(getApplicationContext(), "connecting", Toast.LENGTH_LONG).show();
                        receiveDataFromServer();
                    }).start();
                } catch (Exception e) {
                    Log.d("catcherr", "problem 1");
                }
            }
        }else {
            Toast.makeText(this, "Please Enter IP", Toast.LENGTH_LONG).show();
        }
    }
    protected void receiveDataFromServer() {
        try {
            btGcconnect.setClickable(false);
            runOnUiThread(() -> {
                 btGcconnect.setText("CONNECTING...");
                status = 1;
            });
            socket = new Socket(etHubip.getText().toString().trim(), ports);
            runOnUiThread(() -> {
               // Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                btGcconnect.setText("CONNECTED");
                status = 1;
            });
            inr = socket.getInputStream();
            or = socket.getOutputStream();
            socket.setSoTimeout(60000);

            int charsRead = 0;
            byte[] buffer = new byte[1];
            while ((charsRead = inr.read(buffer)) != -1) {
                process_data(buffer);
            }
            inr.close();
            or.close();
            socket.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  //  Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                    status = 0;
                    btGcconnect.setText("CONNECT");
                    btGcconnect.setClickable(true);
                }
            });

        } catch (Exception e) {
            // receiveDataFromServer();
            runOnUiThread(() -> {
                //Toast.makeText(getApplicationContext(), "Device Disconnected", Toast.LENGTH_SHORT).show();
                btGcconnect.setText("CONNECT");
                btGcconnect.setClickable(true);
                status = 0;
            });
            e.printStackTrace();
            Log.d("catch buff error", "problem");
        }
    }

    protected void process_data(byte[] buff) {

        buffstring = buffstring + new String(buff);

        if (buffstring.equals("done")) {

            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Setting Done", Toast.LENGTH_SHORT).show());
            buffstring = "";
        }
    }

    protected void send_data(byte[] buf) {
        final byte[] buf1 = buf;
        new Thread(() -> {
            try {
                or.write(buf1);
                or.flush();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Sent Successful ", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Please Connect First", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
