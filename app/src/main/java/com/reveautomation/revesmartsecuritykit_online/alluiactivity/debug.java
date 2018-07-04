package com.reveautomation.revesmartsecuritykit_online.alluiactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class debug extends AppCompatActivity {
    @BindView(R.id.bt_get)
    Button scanToggleButton;
    @BindView(R.id.tv_systemflag)
    EditText systemflag;
    static debug Instance;
    public static debug getInstance(){
        return Instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        debug.Instance = this;
        ButterKnife.bind(this);
        systemflag.setEnabled(false);
    }
    public void SetText(String Data){
        this.runOnUiThread(() -> {
            try {
                systemflag.setText(Data);
            }catch (Exception e){

            }
        });
    }
    @OnClick(R.id.bt_get)
    public void onGet() {
        DashboardActivity.getSInstance().tcpwriter("GetFlag");
        Log.d("Click","Success");
    }
}
