package com.reveautomation.revesmartsecuritykit_online.example1_scanning;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.polidea.rxandroidble.exceptions.BleScanException;
import com.polidea.rxandroidble.scan.ScanResult;
import com.reveautomation.revesmartsecuritykit_online.DeviceActivity;
import com.reveautomation.revesmartsecuritykit_online.MyApplication;
import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.ReveSensorEvent;
import com.reveautomation.revesmartsecuritykit_online.alluiactivity.GcHubSync;
import com.reveautomation.revesmartsecuritykit_online.alluiactivity.SelectProductActivity;
import com.reveautomation.revesmartsecuritykit_online.alluiactivity.Settings;
import com.reveautomation.revesmartsecuritykit_online.alluiactivity.debug;
import com.reveautomation.revesmartsecuritykit_online.db.DatabaseHelper;
import com.reveautomation.revesmartsecuritykit_online.db.SessionManager;
import com.reveautomation.revesmartsecuritykit_online.example4_characteristic.CharacteristicOperationExampleActivity;
import com.reveautomation.revesmartsecuritykit_online.model.Model;
import com.reveautomation.revesmartsecuritykit_online.model.MyLocks;
import com.reveautomation.revesmartsecuritykit_online.services.Constants;
import com.reveautomation.revesmartsecuritykit_online.services.MqttMessageService;
import com.reveautomation.revesmartsecuritykit_online.services.PahoMqttClient;
import com.reveautomation.revesmartsecuritykit_online.services.TcpServerService;
import com.reveautomation.revesmartsecuritykit_online.services.UdpServerServices;
import com.reveautomation.revesmartsecuritykit_online.util.HexString;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.reveautomation.revesmartsecuritykit_online.Constraints.APIKEY;
import static com.reveautomation.revesmartsecuritykit_online.Constraints.HttpUrl;
import static com.reveautomation.revesmartsecuritykit_online.Constraints.TASKSDIR;
public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String EXTRA_MAC_ADDRESS = "extra_mac_address";
    static DashboardActivity Instance;
    public PahoMqttClient pahoMqttClient;
    public MqttAndroidClient client;
    final Context context = this;
    public int gwconnection = 0;
    @BindView(R.id.scan_toggle_btn)
    Button scanToggleButton;
    @BindView(R.id.scan_results)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.imgsystem)
    ImageButton imgSystem;
    @BindView(R.id.imgalertoff)
    ImageButton imgAlertoff;
    @BindView(R.id.imgsos)
    ImageButton imgSos;
    @BindView(R.id.imglog)
    ImageButton imgLog;
    @BindView(R.id.tv_connection)
    TextView tv_connection;
    @BindView(R.id.pingcount)
    TextView tv_count;
    ArrayList<Model> list;
    MultiViewTypeAdapter adapter;
    public HashMap devices, deviceposition,idnamemap;
    int typeposi = 32, eventpos = 36, sos = 0;//27 31
    String ZONELIST ="";
    public String GCSENSORSTR="";
    long pingcount = 0;
    public PrintWriter os = null;
    static String curalert = "";
    public static String alertlist = "";
    private int allradyplayed = 0;
    Context mContext;
    MediaPlayer mPlayer;
    byte[] bt;
    public static int sysstatus = 0;
    /*private RxBleClient rxBleClient;
    private Subscription scanSubscription;
    private ScanResultsAdapter resultsAdapter;*/
    public DatabaseHelper databaseHelper;
    SessionManager session;
    public static ReveSensorEvent re;
    // private MyListAdapterList myListAdapterList = null;
    private LinearLayout lin_DeviceList, lin_operationbuttons, lin_NoDevice;
    private ArrayList<MyLocks> arrSavedMyLocks = new ArrayList<>();
    public static DashboardActivity getSInstance() {
        return Instance;
    }

    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }
    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(DashboardActivity.getSInstance().getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(DashboardActivity.getSInstance().getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        session.getGatewaylist();
        APIKEY = session.isApiKey();
        mContext = getApplicationContext();

        devices = new HashMap<String, String>();

        idnamemap = new HashMap<String, Integer>();
        deviceposition = new HashMap<String, List<Integer>>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        pahoMqttClient = new PahoMqttClient();
        re = new ReveSensorEvent();
        //ListSubGW();
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        mPlayer = MediaPlayer.create(mContext, R.raw.sound);
        String output = sb.toString();
        System.out.println(output);
        Constants.CLIENT_ID = output;
        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        sysstatus = session.isSysstatus();
        if (sysstatus == 1) {
            imgSystem.setImageResource(R.drawable.arm);
        } else {
            imgSystem.setImageResource(R.drawable.diarm);
        }
        //lin_operationbuttons = (LinearLayout) findViewById(R.id.operationbuttons);
        //lin_DeviceList = (LinearLayout) findViewById(R.id.DeviceList);
        // lin_NoDevice = (LinearLayout) findViewById(R.id.NoDevice);
        //setBluetooth(true);
        DashboardActivity.Instance = this;
      /*"Ble comment"
        rxBleClient = SampleApplication.getRxBleClient(this);
        configureResultList();*/
        createMyDatabase();
        autoSave();
        list = new ArrayList<>();
        int position = 0;
        arrSavedMyLocks = databaseHelper.getMyLocks();
        ZONELIST="";
        for (int i = 0; i < arrSavedMyLocks.size(); i++) {
            MyLocks mm = arrSavedMyLocks.get(i);
            String name = mm.lockName;
            int id = mm.id;
            String mac = mm.lockMacAddress;
            String zone_list = mm.Zone_list;
            int type = mm.type;
            devices.put(mac, name);
            idnamemap.put("Name: "+name+" Mac: "+mac+"Zone List: "+zone_list,id);
            ZONELIST = ZONELIST+mac+","+name+","+zone_list+","+type+",";
            GCSENSORSTR = GCSENSORSTR+mac+","+name+"|";
            Log.d("Zone list", ZONELIST);
            List<Integer> list1 = new ArrayList<>();
            if (type != 7) {
                list1.add(type);
                list1.add(position);
                position++;
            }
            deviceposition.put(mac, list1);
            if (type == 0) {
                list.add(new Model(type, name, 0));
            } else if (type == 1) {
                list.add(new Model(Model.DOOR_TYPE, "Door Close", name, R.drawable.door_close));
            } else if (type == 2) {
                list.add(new Model(Model.PIR_TYPE, "No Event", name, R.drawable.pirnotdetect));
            } else if (type == 3) {
                list.add(new Model(Model.CARD_TYPE, "-", name, R.drawable.card));
            } else if (type == 4) {
                // updateS canResult(new Model(viewtype, mac,mac,humidity,temperature), pos);
                list.add(new Model(Model.WEATHER_TYPE, "-", name, 0));
            } else if (type == 5) {
                list.add(new Model(Model.SOIL_TYPE, "-", name, 0));
            }else if (type == 6) {
                list.add(new Model(Model.DOOR_TYPE, "Window Close", name, R.drawable.door_close));
            }
            Log.d("Name ", name + " Mac: " + mac+" position");
        }
        //tcpwriter("mac_list\n"+ZONELIST);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        adapter = new MultiViewTypeAdapter(list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, SelectProductActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
        });
        re.setOnNewsUpdateListener(datas -> {
            eventGenerator(datas);
            Log.d("data On news", datas);
        });
       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String username = session.isName();
        if (!username.equals("gcdemo")) {
           // Intent intent = new Intent(DashboardActivity.this, MqttMessageService.class);
           // startService(intent);
            Intent intent1 = new Intent(DashboardActivity.this, UdpServerServices.class);
            startService(intent1);
        }else {
            Intent intent1 = new Intent(DashboardActivity.this, TcpServerService.class);
            startService(intent1);
        }
        final Handler handler = new Handler();
        Timer timer = new Timer(false);

        int c = 0;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
               /* handler.post(() ->
                        Log.d("TEST","COUNT")
                );*/
                if (gwconnection == 0){
                    DashboardActivity.this.runOnUiThread(() -> {
                        //Here your code that runs on UI Threads
                        tv_connection.setText("Disconnected");
                        gwconnection = 0;
                    });
                }else {
                    DashboardActivity.this.runOnUiThread(() -> {;
                        //Here your code that runs on UI Threads
                        tv_connection.setText("Connected");
                        gwconnection = 0;
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 3000, 3000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.moveTaskToBack(true);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate fthe menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @OnClick(R.id.imgsystem)
    public void onImgsystemClick() {
        {
            sysstatus = session.isSysstatus();
            if (sysstatus == 1) {
                try {
                     DashboardActivity.getSInstance().tcpwriter("RSSK_DISARM");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                session.addSysstatus(0);
                DashboardActivity.getSInstance().alertlistappend("Disarm_(S)_",0);
                DashboardActivity.getSInstance().playerStop();
                imgSystem.setImageResource(R.drawable.diarm);
            } else if (sysstatus == 0){
                try {
                    DashboardActivity.getSInstance().tcpwriter("RSSK_ARM");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                session.addSysstatus(1);
                DashboardActivity.getSInstance().alertlistappend("Arm_(S)____",0);
                imgSystem.setImageResource(R.drawable.arm);
            }
        }
    }
    @OnClick(R.id.imglog)
    public void onImglogClick() {
        dialogBox_alert_list();
    }
    @OnClick(R.id.imgalertoff)
    public void onAlertOffClick() {
        tcpwriter("RSSK_OFF");
    }
    @OnClick(R.id.imgsos)
    public void onImgSosClick() {
       tcpwriter("RSSK_SOS");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_debug) {
            final Intent intent = new Intent(this, debug.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            session.logoutUser();
            finish();
            return true;
        }
        else if (id == R.id.action_sync) {
            Toast.makeText(context,  ZONELIST, Toast.LENGTH_SHORT).show();
            tcpwriter("mac_list," +ZONELIST);
            return true;
        }
        else if (id == R.id.action_gcsync) {
            final Intent intent = new Intent(this, GcHubSync.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void stopBroker(){
        stopService(new Intent(DashboardActivity.this, MqttMessageService.class));
    }
    public void startBroker(){
        client = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);
        startService(new Intent(DashboardActivity.this, MqttMessageService.class));
    }

    //backup
   /* void updateScanResult(Model model) {
        // Not the best way to ensure distinct devices, just for sake on the demo.
        try {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).mac.equals(model.mac)) {
                    list.set(i, model);
                    adapter.notifyItemChanged(i);
                    return;
                }
            }
        } catch (Exception es) {

        }

    }*/

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void updateScanResult(Model model, int position) {
        // Not the best way to ensure distinct devices, just for sake on the demo.

        Handler handler = new Handler(Looper.getMainLooper());
        Runnable r = () -> {
            //what ever you do here will be done after 3 seconds delay.
            try {
                list.set(position, model);
                adapter.notifyItemChanged(position);
                return;
            } catch (Exception es) {

            }
        };
        handler.postDelayed(r, 0);
    }
    public void SetText(String Text){
        tv_connection.setText(Text);
    }
    public void tcpwriter(String data){
        //if (gwconnection != 0) {
       /* try {
            InetAddress IPAddress = InetAddress.getByName("255.255.255.255");
            byte[] datas = data.getBytes();
            DatagramPacket replyPacket = new DatagramPacket(datas, datas.length, IPAddress, 5555);
            UdpServerServices.getInstance().socket.send(replyPacket);
            Log.d("Dashboar TCP","end");
        }catch (Exception e){
            e.printStackTrace();
        }*/
            try {
                DashboardActivity.getSInstance().pahoMqttClient.publishMessage(DashboardActivity.getSInstance().client, data, 1, Constants.PUBLISH_TOPIC);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (Object variableName : TcpServerService.getInstance().tcpclients.keySet()) {
                    PrintWriter pw;
                    pw = (PrintWriter) TcpServerService.getInstance().tcpclients.get(variableName);
                    if (pw != null) {
                        pw.print(data);
                        pw.flush();
                    }
                }
            } catch (Exception e) {

            }
       // }
    }

    private void autoSave() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            MyLocks myLocks = new MyLocks();
            myLocks.lockMacAddress = b.getString("Mac");
            myLocks.lockName = b.getString("Name");
            myLocks.type = b.getInt("Type");
            myLocks.Zone_list = b.getString("Zone_list");
            Toast.makeText(context,  b.getString("Zone_list"), Toast.LENGTH_SHORT).show();
            int id = databaseHelper.insertLockData(myLocks);
            if (id <= 0) {
                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
            } else {
                arrSavedMyLocks = databaseHelper.getMyLocks();
                //  myListAdapterList.notifyDataSetChanged();
                Toast.makeText(context, "Inserted " + myLocks.type, Toast.LENGTH_SHORT).show();
            }
            getIntent().removeExtra("Mac");
            getIntent().removeExtra("Name");
            getIntent().removeExtra("Type");
            getIntent().removeExtra("Zone_list");
            b.clear();
        }
        if (arrSavedMyLocks.size() == 0) {
            // lin_operationbuttons.setVisibility(View.GONE);
            // lin_DeviceList.setVisibility(View.GONE);
            // lin_NoDevice.setVisibility(View.VISIBLE);
        } else {
            // lin_operationbuttons.setVisibility(View.GONE);
            // lin_DeviceList.setVisibility(View.VISIBLE);
            //lin_NoDevice.setVisibility(View.GONE);
        }
    }

    private void createMyDatabase() {
        try {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(DashboardActivity.this);
                databaseHelper.createDatabase();
            }
            Log.d("Database", databaseHelper.currentDatabaseFilePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(DashboardActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            // Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.scan_toggle_btn)
    public void onScanToggleClick() {
        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 123);
       /* "Ble comment"
        if (isScanning()) {
              scanSubscription.unsubscribe();
        } else {
            onScanstart();
        }*/
    }

    void addScanResult(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.
        // final RxBleDevice bleDevice = bleScanResult.getBleDevice();
        // String chk = HexString.bytesToHex(bleScanResult.getScanRecord().getBytes()).trim();
        try {
            String mac = bleScanResult.getBleDevice().getMacAddress();
            //System.out.println("Data  " + HexString.bytesToHex(bt));
            bt = bleScanResult.getScanRecord().getBytes();
            Log.d("Mac ", mac);
            Log.d("Data  ", HexString.bytesToHex(bt) + "Mac " + mac);
            int pos = -1;
            if (devices.containsKey(mac)) {
                String dName = String.valueOf(devices.get(mac));
                List<Integer> d_list = (List<Integer>) deviceposition.get(mac);
                int type = d_list.get(0);
                pos = d_list.get(1);
                Log.d("IN IF ", mac);//23 02 and  24 b2 deci 178    32 eventstatus
                int typeofsens = bt[typeposi] & 0XFF;
                int typeofevent = bt[eventpos] & 0XFF;
                if (typeofsens == 11) {
                    if (bt[eventpos] == 26)//Arm
                    {

                    }
                } else if (bt[typeposi] == 9 || bt[typeposi] == 12) {
                    //Door
                    if (bt[eventpos] == 30)//event
                    {
                        updateScanResult(new Model(type, "Door Open", dName, R.drawable.door_open), pos);
                    } else if (bt[eventpos] == 31)//not event
                    {
                        updateScanResult(new Model(type, "Door Close", dName, R.drawable.door_close), pos);
                    }
                } else if (typeofsens == 100) {
                    //Door
                    Double a = Double.valueOf((typeofevent * 100) / 255.0);
                    updateScanResult(new Model(type, String.format("%.2f", a) + " %", dName, 0), pos);

                } else if (bt[typeposi] == 10) {
                    //Pir
                    if (bt[eventpos] == 30)//event
                    {
                        updateScanResult(new Model(type, "Pir Detected", dName, R.drawable.pirdetect), pos);
                        Handler handler = new Handler(Looper.getMainLooper());
                        int finalPos = pos;
                        Runnable r = () -> {
                            //what ever you do here will be done after 3 seconds delay.
                            updateScanResult(new Model(type, "No Event", dName, R.drawable.pirnotdetect), finalPos);
                        };
                        handler.postDelayed(r, 2000);
                    }
                }
                //updateScanResult();
        /*updateScanResult(new Model(0, "palsana",bleDevice.getName() , 0));*/
            }
        } catch (Exception es) {

        }
    }

    void eventGenerator(String data) {
if (data.contains("System Flag")){
    try {
        debug.getInstance().SetText(data);
    }catch (Exception e){

    }
}else if (data.contains("SoftPing")){
    try {
        Toast.makeText(this, "SoftPing Received", Toast.LENGTH_SHORT).show();
    }catch (Exception e){

    }
}else if (data.contains("ping")){
    try {
        tv_count.setText( data+ pingcount++);
            gwconnection++;
    }catch (Exception e){

    }
}else  {
    {
        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(data);

            for (int i = 0; i < jsonarray.length(); i++)
                try {
                    int pos = -1;
                    JSONObject jsonobj = jsonarray.getJSONObject(i);
                    String type = jsonobj.getString("type");
                    String mac = jsonobj.getString("mac");
                    int actlog = jsonobj.getInt("actlog");
                    //String rssi = jsonobj.getString("rssi");
                    //String battery = jsonobj.getString("battery");
                    String event = "";
                    int viewtype = 0;
                    if (!(type.equals("soil") || type.equals("S1"))) {
                        event = jsonobj.getString("event");
                    }
                    if (devices.containsKey(mac)) {
                        List<Integer> d_list = (List<Integer>) deviceposition.get(mac);
                        String dName = String.valueOf(devices.get(mac));
                        if (!type.equals("panic")) {
                            viewtype = d_list.get(0);
                            pos = d_list.get(1);
                        }
                        System.out.println(pos);
                        if (type.equals("pir")) {
                            alertlistappend(dName + "\nPir Detected",actlog);
                            curalert = "Pir Detected";
                            playerStart(actlog);
                            updateScanResult(new Model(viewtype, "No Event", dName, R.drawable.pirdetect), pos);
                            Handler handler = new Handler(Looper.getMainLooper());
                            int finalPos = pos;
                            int vt = viewtype;
                            Runnable r = () -> {
                                //what ever you do here will be done after 3 seconds delay.
                                updateScanResult(new Model(vt, "No Event", dName, R.drawable.pirnotdetect), finalPos);
                            };
                            handler.postDelayed(r, 2000);
                        } else if (type.equals("door") && viewtype == 1) {
                            if (event.equals("open"))//event
                            {
                                alertlistappend(dName + "\nDoor_Open_",actlog);
                                curalert = "Door Open";
                                updateScanResult(new Model(viewtype, "Door Open", dName, R.drawable.door_open), pos);
                                playerStart(actlog);
                            } else if (event.equals("close"))//not event
                            {
                                alertlistappend(dName + "\nDoor Close",actlog);
                                updateScanResult(new Model(viewtype, "Door Close", dName, R.drawable.door_close), pos);
                            }
                        } else if (type.equals("window") && viewtype == 6) {
                            if (event.equals("open"))//event
                            {
                                alertlistappend(dName + "\nWindowOpen",actlog);
                                curalert = "Window Open";
                                updateScanResult(new Model(viewtype, "Window Open", dName, R.drawable.door_open), pos);
                                playerStart(actlog);
                            } else if (event.equals("close"))//not event
                            {
                                alertlistappend(dName + "\nWindow Close",actlog);
                                updateScanResult(new Model(viewtype, "Window Close", dName, R.drawable.door_close), pos);
                            }
                        } else if (type.equals("panic")) {
                            if (event.equals("arm")) {
                                DashboardActivity.this.runOnUiThread(() -> {
                                    imgSystem.setImageResource(R.drawable.arm);
                                    //Here your code that runs on UI Threads
                                });

                                session.addSysstatus(1);
                                alertlistappend(dName + "\nArm_(P)____",actlog);
                            } else if (event.equals("disarm")) {
                                DashboardActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        imgSystem.setImageResource(R.drawable.diarm);
                                        //Here your code that runs on UI Threads
                                    }
                                });
                                session.addSysstatus(0);
                                alertlistappend(dName + "\nDisarm_(P)_",actlog);
                            } else if (event.equals("sos")) {
                                alertlistappend(dName + "\nSoS_(P)____",actlog);
                                curalert = "SoS button pressed";
                                sos = 1;
                                playerStart(actlog);
                            } else if (event.equals("alert")) {
                                curalert = "Alert button pressed";
                                alertlistappend(dName + "\nAlert_(P)___",actlog);
                                playerStart(actlog);
                            }
                        } else if (type.equals("soil")) {
                            int moisture = jsonobj.getInt("moisture");
                            Double a = Double.valueOf((moisture * 100) / 255.0);
                            updateScanResult(new Model(viewtype, String.format("%.2f", a) + " %", dName, 0), pos);
                        } else if (type.equals("S1")) {
                            String humidity = jsonobj.getString("humidity");
                            String temperature = jsonobj.getString("temperature");
                            updateScanResult(new Model(viewtype, dName, humidity, temperature), pos);
                        }
                    } else if (type.equals("status")) {
                        Log.d("Status: ",event);
                        if (event.equals("arm")) {
                            DashboardActivity.this.runOnUiThread(() -> {
                                imgSystem.setImageResource(R.drawable.arm);
                                //Here your code that runs on UI Threads
                            });
                           // tcpwriter("mac_list\n" + ZONELIST);
                            session.addSysstatus(1);
                        } else if(event.equals("disarm")) {
                            DashboardActivity.this.runOnUiThread(() -> {
                                imgSystem.setImageResource(R.drawable.diarm);
                                //Here your code that runs on UI Threads
                            });
                            //tcpwriter("mac_list\n" + ZONELIST);
                            session.addSysstatus(0);
                        }else if(event.equals("enable")){
                            try {
                                Settings.getInstance().setStatus("PIR ENABLE");
                                alertlistappend("Pir Enable__",actlog);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else if(event.equals("disable")){
                            try {
                                Settings.getInstance().setStatus("PIR DISABLE");
                                alertlistappend("Pir Disable_",actlog);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }/*else if (type.equals("panic")){
                            if (event.equals("arm")){
                                session.addSysstatus(1);
                                alertlistappend("Arm_(P)____");
                            }else if (event.equals("disarm")){
                                session.addSysstatus(0);
                                alertlistappend("Disarm_(P)_");
                            }else if (event.equals("sos")){
                                alertlistappend("SoS_(P)____");
                                curalert = "SoS button pressed";
                                sos = 1;
                                playerStart();
                            }else if (event.equals("alert")){
                                curalert = "Alert button pressed";
                                alertlistappend("Alert_(P)___");
                                playerStart();
                            }
                        }*/
                } catch (Exception eeed) {
                    eeed.printStackTrace();
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    try {
        String mac = "macparsh";
        //System.out.println("Data  " + HexString.bytesToHex(bt));


        int pos = -1;
        if (devices.containsKey(mac)) {
            String dName = String.valueOf(devices.get(mac));
            List<Integer> d_list = (List<Integer>) deviceposition.get(mac);
            int type = d_list.get(0);
            pos = d_list.get(1);
            Log.d("IN IF ", mac);//23 02 and  24 b2 deci 178    32 eventstatus
            int typeofsens = bt[typeposi] & 0XFF;
            int typeofevent = bt[eventpos] & 0XFF;
            if (typeofsens == 11) {
                if (bt[eventpos] == 26)//Arm
                {

                }
            } else if (bt[typeposi] == 9 || bt[typeposi] == 12) {
                //Door
                if (bt[eventpos] == 30)//event
                {
                    updateScanResult(new Model(type, "Door Open", dName, R.drawable.door_open), pos);
                } else if (bt[eventpos] == 31)//not event
                {
                    updateScanResult(new Model(type, "Door Close", dName, R.drawable.door_close), pos);
                }
            } else if (typeofsens == 100) {
                //Door
                Double a = Double.valueOf((typeofevent * 100) / 255.0);
                updateScanResult(new Model(type, String.format("%.2f", a) + " %", dName, 0), pos);

            } else if (bt[typeposi] == 10) {
                //Pir
                if (bt[eventpos] == 30)//event
                {
                    updateScanResult(new Model(type, "Pir Detected", dName, R.drawable.pirdetect), pos);
                    Handler handler = new Handler(Looper.getMainLooper());
                    int finalPos = pos;
                    Runnable r = () -> {
                        //what ever you do here will be done after 3 seconds delay.
                        updateScanResult(new Model(type, "No Event", dName, R.drawable.pirnotdetect), finalPos);

                    };
                    handler.postDelayed(r, 2000);
                }
            }

            //updateScanResult();
            /*updateScanResult(new Model(0, "palsana",bleDevice.getName() , 0));*/
        }
    } catch (Exception es) {

    }
}

}

    private void handleBleScanException(BleScanException bleScanException) {
        final String text;

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                text = "Bluetooth is not available";
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                text = "Enable bluetooth and try again";
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                text = "On Android 6.0 location permission is required. Implement Runtime Permissions";
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                text = "Location services needs to be enabled on Android 6.0";
                break;
            case BleScanException.SCAN_FAILED_ALREADY_STARTED:
                text = "Scan with the same filters is already started";
                break;
            case BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                text = "Failed to register application for bluetooth scan";
                break;
            case BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED:
                text = "Scan with specified parameters is not supported";
                break;
            case BleScanException.SCAN_FAILED_INTERNAL_ERROR:
                text = "Scan failed due to internal error";
                break;
            case BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES:
                text = "Scan cannot start due to limited hardware resources";
                break;
            case BleScanException.UNDOCUMENTED_SCAN_THROTTLE:
                text = String.format(
                        Locale.getDefault(),
                        "Android 7+ does not allow more scans. Try in %d seconds",
                        secondsTill(bleScanException.getRetryDateSuggestion())
                );
                break;
            case BleScanException.UNKNOWN_ERROR_CODE:
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                text = "Unable to start scanning";
                break;
        }
        Log.w("EXCEPTION", text, bleScanException);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private long secondsTill(Date retryDateSuggestion) {
        return TimeUnit.MILLISECONDS.toSeconds(retryDateSuggestion.getTime() - System.currentTimeMillis());
    }
    @Override
    public void onPause() {
        MyApplication.activityPaused();
        super.onPause();

/*    "Ble comment"
 if (isScanning()) {
             * Stop scanning in onPause callback. You can use rxlifecycle for convenience. Examples are provided later.

            // scanSubscription.unsubscribe();
        }*/
    }

    @Override
    public void onResume() {
        MyApplication.activityResumed();
        sysstatus = session.isSysstatus();
        if (sysstatus == 1) {
            imgSystem.setImageResource(R.drawable.arm);
        } else {
            imgSystem.setImageResource(R.drawable.diarm);
        }
        super.onResume();
        //onScanstart();

    }


    /* "Ble comment"
    public void onScanstart() {
         if (!isScanning()) {
             *//*
             * Stop scanning in onPause callback. You can use rxlifecycle for convenience. Examples are provided later.
             *//*
            scanSubscription = rxBleClient.scanBleDevices(
                    new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                            .build()
                    ///,new ScanFilter.Builder().setDeviceName("Reve_Door_Sensor")
                    // add custom filters if needed
                    //.build()
            )
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnUnsubscribe(this::clearSubscription)
                    .subscribe(this::addScanResult, this::onScanFailure);
            updateButtonUIState();
        }
    }

    private void configureResultList() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        resultsAdapter = new ScanResultsAdapter();
        recyclerView.setAdapter(resultsAdapter);
        resultsAdapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
        });
    }

    private boolean isScanning() {
        return scanSubscription != null;
    }
*/
    private void onAdapterItemClick(ScanResult scanResults) {
        final String macAddress = scanResults.getBleDevice().getMacAddress();
        //final Intent intent = new Intent(this, DeviceActivity.class);
        // intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, macAddress);

        final Intent intent = new Intent(this, CharacteristicOperationExampleActivity.class);
        // If you want to check the alternative advanced implementation comment out the line above and uncomment one below
        // final Intent intent = new Intent(this, AdvancedCharacteristicOperationExampleActivity.class);
        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, macAddress);
        startActivity(intent);

    }

    private void onScanFailure(Throwable throwable) {

        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
    }
/* "Ble comment"
    private void clearSubscription() {
        scanSubscription = null;
        //  resultsAdapter.clearScanResults();
        updateButtonUIState();
    }

    private void updateButtonUIState() {
        scanToggleButton.setText(isScanning() ? R.string.stop_scan : R.string.start_scan);

    }
*/

    public void ListSubGW() {
        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUrl + TASKSDIR,
                ServerResponse -> {
                    // Hiding the progress dialog after all task complete.
                    Log.d("Server Responce", ServerResponse);
                    // Matching server responce message to our text.
                    try {
                        JSONObject jsonObject = new JSONObject(ServerResponse);
                        JSONArray array = jsonObject.getJSONArray("gatewaylist");
                        Boolean error = jsonObject.getBoolean("error");
                        if (error != true) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject c = null;
                                try {
                                    c = array.getJSONObject(i);
                                    int id = c.getInt("id");
                                    String gateway_mac = c.getString("gateway_mac");
                                    session.addInGatewayList(gateway_mac);
                                    //  int id = c.getInt("id");
                                    //   String gateway_mac = c.getString("gateway_mac");
                                    // String apiKey = c.getString("apiKey");
                                    Log.d("Add in to list:", gateway_mac);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> {
                    // Showing error message if something goes wrong.
                    Toast.makeText(context, volleyError.toString(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("apikey", APIKEY);
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
    public void alertlistappend(String alert, int actlog) {
        if (actlog == 0 || actlog == 2){
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        alert = alert + "  " + formattedDate;
        alertlist = session.isAlertList();
        session.addAlertList(alert + "\n" + alertlist);
        }
    }

    public void dialogBox_alert_list() {
        alertlist = session.isAlertList();

        AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(DashboardActivity.this, R.style.MyDialogTheme);
        alertDialogBuilder1.setTitle("Alert Log");
        alertDialogBuilder1.setMessage(alertlist);
        alertDialogBuilder1.setNegativeButton("Cancel",
                (arg0, arg1) -> playerStop());

        AlertDialog alertDialog1 = alertDialogBuilder1.create();
        alertDialog1.show();
    }
    public void playerStart(int actlog) {
            sysstatus = session.isSysstatus();
        if (((sysstatus == 1 ) || sos == 1) && (actlog == 2 || actlog == 1)) {
            if (allradyplayed == 0) {
                allradyplayed = 1;
                {
                    if (MyApplication.isActivityVisible() == false) {
                        Intent intentHome = new Intent(getApplicationContext(), DashboardActivity.class);
                        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentHome);
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable r = () -> {
                        //what ever you do here will be done after 3 seconds delay.
                        playerStop();
                        mPlayer = MediaPlayer.create(mContext, R.raw.sound);
                        mPlayer.setLooping(true);
                        mPlayer.start();
                        try {
                            dialogBox();
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    };
                    handler.postDelayed(r, 1000);
                }
            }
        }
    }
    public void dialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashboardActivity.this,R.style.MyDialogTheme);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Security Alert");
        alertDialogBuilder.setMessage(curalert);
        alertDialogBuilder.setPositiveButton("Show Alert",
                (arg0, arg1) -> {
                    playerStop();
                    dialogBox_alert_list();
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                (arg0, arg1) -> playerStop());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void playerStop() {
        if (allradyplayed == 1) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                sos = 0;
                allradyplayed = 0;
            }
        }
    }
}