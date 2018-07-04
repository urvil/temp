package com.reveautomation.revesmartsecuritykit_online.alluiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.db.SessionManager;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.imgdelete)
    ImageButton imgDelete;
    @BindView(R.id.bt_switch_pir)
    Button btSwitchpir;
    SessionManager session;
    int sysstatus = 0;
    int ids = -1;
    HashMap devicemapping;
    HashMap zonelistmapping;
    String item ="";
    static Settings Instance;
    public static Settings getInstance(){
        return Instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Settings.Instance = this;
        ButterKnife.bind(this);
        devicemapping = new HashMap<String, Integer>();
        session = new SessionManager(getApplicationContext());
        sysstatus = session.isSysstatus();
        DashboardActivity.getSInstance().tcpwriter("RSSK_CHANGE_PIR_ZONE");
        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        devicemapping = DashboardActivity.getSInstance().idnamemap;

        // Spinner Drop down elements
        List<String> categories = new ArrayList <String>(devicemapping.keySet());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        item = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
       ids = (int) devicemapping.get(item);
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @OnClick(R.id.imgdelete)
    public void onImgDeleteClick() {
        if (ids != -1) {
         Boolean status =   DashboardActivity.getSInstance().databaseHelper.deleteSensorFromId(ids);
           if (status = true) {
               DashboardActivity.getSInstance().finish();
               // Toast.makeText(getApplicationContext(), strSensorMac +"\n"+strSensorName, Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(Settings.this, DashboardActivity.class);
               startActivity(intent);
               finish();
           }else {
               Toast.makeText(this, "Failed to delete:"+item, Toast.LENGTH_LONG).show();
           }
        }else {
            Toast.makeText(this, "Sensor not available"+item, Toast.LENGTH_LONG).show();
        }
    }
    @OnClick(R.id.bt_switch_pir)
    public void onBtSwitchPirClick() {
        DashboardActivity.getSInstance().tcpwriter("RSSK_CHANGE_PIR_ZONE");
    }
    public void setStatus(String data){
        this.runOnUiThread(() -> {
            btSwitchpir.setText(data);
        });
    }

    public void dialogBox_alert_list() {
        DashboardActivity.getSInstance().alertlist = session.isAlertList();

        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(Settings.this, R.style.MyDialogTheme);
        alertDialogBuilder1.setTitle("Alert Log");
        alertDialogBuilder1.setMessage(DashboardActivity.getSInstance().alertlist);
        alertDialogBuilder1.setNegativeButton("Cancel",
                (arg0, arg1) -> DashboardActivity.getSInstance().playerStop());
        AlertDialog alertDialog1 = alertDialogBuilder1.create();
        alertDialog1.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        DashboardActivity.getSInstance().tcpwriter("RSSK_CHANGE_PIR_ZONE");
        //onScanstart();
    }
}
