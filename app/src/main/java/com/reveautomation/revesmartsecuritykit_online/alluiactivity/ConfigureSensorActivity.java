package com.reveautomation.revesmartsecuritykit_online.alluiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ConfigureSensorActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    @BindView(R.id.btnAdd)
    Button btnAdd;

    private EditText etSensorName, etSensorMac;
    private String strSensorName, strSensorMac;
    private int Type = 0;
    String item="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_sensor);
        initi();
        String[] country = { "Silent", "Entry-Exit", "Instance", "Panic", "Arm-Inside"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
        // getData();
      /*  etSensorMac.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (etSensorMac.length() == 2 || etSensorMac.length() == 4 || etSensorMac.length() == 6 || etSensorMac.length() == 8) {
                    String mac =  etSensorMac.getText().toString().trim();
                    if (mac.contains(":")) {
                        mac.replace(":", "");
                    }
                    if (etSensorMac.length()>3) {
                        mac = mac.replaceAll("(.{2})", "$1" + ":").substring(0, etSensorMac.length());
                        etSensorMac.setText(mac);
                    }
                }
            }
        });*/
    }

    private void getData() {
        //  Bundle b = getIntent().getExtras();
        //  strMacAddress = b.getString("MacAddress");
    }

    private void initi() {
        ButterKnife.bind(this);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            Type = b.getInt("Type");
        }
        etSensorName = (EditText) findViewById(R.id.etSensorname);
        etSensorMac = (EditText) findViewById(R.id.etSensorMac);
    }

    @OnClick(R.id.btnAdd)
    public void onClickAdd() {
        strSensorName = etSensorName.getText().toString().trim();
        strSensorMac = etSensorMac.getText().toString().trim();
        Toast.makeText(this, item,
                Toast.LENGTH_SHORT).show();
        if (strSensorName.isEmpty()) {
            etSensorName.setError("Invalid Name");
        }
        if (strSensorMac.equalsIgnoreCase("")) {
            etSensorMac.setError("Invalid Mac");
        }
        if (!strSensorMac.isEmpty() && !strSensorName.isEmpty() && isMacValid(strSensorMac)) {
            int chkflag = 0;
            if (!item.equals(null)){
                chkflag = 1;
            }
            //bypass zone
            //chkflag = 1;
            if (chkflag == 1) {
                DashboardActivity.getSInstance().finish();
                // Toast.makeText(getApplicationContext(), strSensorMac +"\n"+strSensorName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ConfigureSensorActivity.this, DashboardActivity.class);
                intent.putExtra("Type", Type);
                intent.putExtra("Mac", strSensorMac);
                intent.putExtra("Name", strSensorName);
                intent.putExtra("Zone_list", item);
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this, "Zone is not selected",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Enter valid mac",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isMacValid(String mac) {
        Pattern p = Pattern.compile("^([a-fA-F0-9]{2}[:-]){5}[a-fA-F0-9]{2}$");
        Matcher m = p.matcher(mac);
        return m.find();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       // item = parent.getItemAtPosition(position).toString();
        item = String.valueOf(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
