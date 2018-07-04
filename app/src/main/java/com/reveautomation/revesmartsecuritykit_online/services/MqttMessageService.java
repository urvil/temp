package com.reveautomation.revesmartsecuritykit_online.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.reveautomation.revesmartsecuritykit_online.MyApplication;
import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.ReveSensorEvent;
import com.reveautomation.revesmartsecuritykit_online.db.SessionManager;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

public class MqttMessageService extends Service {

    private static final String TAG = "MqttMessageService";
    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;
    private String result;
    SessionManager session;
    List<String> gatewaylist;
    int count =0;
    public ReveSensorEvent mHandler ;//= new ReveSensorEvent();
    public MqttMessageService() {
    }
    @Override
    public void onCreate() {
        session = new SessionManager(MqttMessageService.this);
        super.onCreate();
        Log.d(TAG, "onCreate");
        pahoMqttClient = new PahoMqttClient();
        mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), Constants.MQTT_BROKER_URL, Constants.CLIENT_ID);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                try {
                   // pahoMqttClient.subscribe(mqttAndroidClient, "/gw/b8:27:eb:d6:34:65/status", 1);
                    pahoMqttClient.subscribe(mqttAndroidClient, Constants.SUBSCRIBE_TOPIC, 1);
                    ///gw/B8:27:EB:25:DE:F3/status"    $REVE/RSSK/GW/b8:27:eb:01:46:37/status
                    gatewaylist= session.getGatewaylist();//$homesecurity/gateway/status
                    /*try {

                        DashboardActivity.getSInstance().pahoMqttClient.publishMessage(DashboardActivity.getSInstance().client, "RSSK_STATUS", 1, Constants.PUBLISH_TOPIC);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }*/
                    if (gatewaylist != null) {
                        for (String gateway : gatewaylist) {
                            pahoMqttClient.subscribe(mqttAndroidClient, "$homesecurity/" + gateway + "/status", 1);
                        }
                    }
                    DashboardActivity.getSInstance().SetText("Connected");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Toast.makeText(DashboardActivity.getSInstance(), "Connected", Toast.LENGTH_SHORT).show();

            }
            @Override
            public void connectionLost(Throwable throwable) {
                DashboardActivity.getSInstance().SetText("Disconnected");
                Toast.makeText(DashboardActivity.getSInstance(), "Disconnected", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                //setMessageNotification(topic,(count++)+new String(mqttMessage.getPayload()));
                result = new String(mqttMessage.getPayload());
               // Log.d("Message  ",count+"  "+result);
                ReveSensorEvent.getInstance().listener.onNewsUpdate(result);
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (MyApplication.isActivityVisible() == false) {
            Intent intent = new Intent("android.intent.category.LAUNCHER");
            intent.setClassName("com.app.androidkt.mqtt", "com.app.androidkt.mqtt.MainActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            sendBroadcast(new Intent("YouWillNeverKillMe"));
        }
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(topic)
                        .setContentText(msg);
        Intent resultIntent = DashboardActivity.getSInstance().getIntent();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());
/*
        Intent intent = new Intent("android.intent.category.LAUNCHER");
        intent.setClassName("com.app.androidkt.mqtt", "com.app.androidkt.mqtt.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
*/
    }
}
