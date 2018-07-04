package com.reveautomation.revesmartsecuritykit_online.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import com.reveautomation.revesmartsecuritykit_online.ReveSensorEvent;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SMSReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;
        String strMessage = "";

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");
            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                //strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                //strMessage += " : ";
                strMessage += messages[i].getMessageBody();
                //strMessage += "\n";
            }
            Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
            if (strMessage.contains(",")) {
                String[] datas = strMessage.split(",");

                String mac = datas[1];
                String type = datas[2];
                String event = datas[3];
                String actlog = datas[4];
                switch (type) {
                    case "0b":
                        type = "panic";
                        break;
                    case "09":
                        type = "door";
                        break;
                    case "0c":
                        type = "window";
                        break;
                    case "0a":
                        type = "pir";
                        break;
                }
                switch (event) {
                    case "1e":
                        event = "open";
                        break;
                    case "1f":
                        event = "close";
                        break;
                    case "1a":
                        event = "arm";
                        break;
                    case "1b":
                        event = "disarm";
                        break;
                    case "1c":
                        event = "sos";
                        break;
                    case "1d":
                        event = "default";
                        break;
                }

                byte b1 = (byte) 0X7F;
                JSONObject obj = new JSONObject();
                JSONArray arr = new JSONArray();
                try {
                    obj.put("type", type);
                    obj.put("event", event);
                    obj.put("actlog", actlog);
                    obj.put("mac", mac);
                    arr.put(obj);
                    String jsonst = arr.toString();
                    Log.d("Json string", jsonst);
                    ReveSensorEvent.getInstance().listener.onNewsUpdate(jsonst);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // sendSMS("8200176345",String.valueOf(b1));
            }
        }
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
}