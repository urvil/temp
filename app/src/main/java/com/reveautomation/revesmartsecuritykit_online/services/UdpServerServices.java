package com.reveautomation.revesmartsecuritykit_online.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
public class UdpServerServices extends Service {

    private static final String TAG = "UdpServerService";
    static UdpServerServices Instance;
    public static DatagramSocket socket;
    static String temp="";
    public InetAddress IPAddress;
    public int udpport ;
    public HashMap udpclients;

    public UdpServerServices() {
    }
    public static UdpServerServices getInstance(){
        return Instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        UdpServerServices.Instance = this;
        udpclients = new HashMap<InetAddress, Socket>();
        new Thread(new BroadcastReciver()).start();
        Log.d(TAG, "onCreate");
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
    }

    public class BroadcastReciver implements Runnable {

        public BroadcastReciver() {

        }

        public synchronized void run() {
            try {
                socket = new DatagramSocket(5556);
                byte[] incomingData = new byte[1024];
                DatagramPacket incomingPacket;
                while (true) {
                    incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                    socket.receive(incomingPacket);
                    IPAddress = incomingPacket.getAddress();
                    udpport = incomingPacket.getPort();
                    temp =  new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength()).trim();
                    //System.out.println("Client IP: " + incomingPacket.getAddress().getHostAddress().trim()+" Port : "+udpport);
                    if(temp.contains("OFFLINE")) {
                        if (!Constants.MQTT_BROKER_URL.trim().equals("tcp://" + incomingPacket.getAddress().getHostAddress().toString() + ":1883")) {
                            temp = temp.substring(11, 28).trim();
                            Log.d(TAG, temp);
                            Constants.SUBSCRIBE_TOPIC = "/gw/" + temp + "/status";
                            Constants.PUBLISH_TOPIC = "/gw/" + temp + "/action";
                            Constants.MQTT_BROKER_URL = "tcp://" + incomingPacket.getAddress().getHostAddress().toString().trim() + ":1883";
                            DashboardActivity.getSInstance().stopBroker();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            DashboardActivity.getSInstance().startBroker();
                        }
                    }
                   /* UdpServerServices.getInstance().udpclients.put(IPAddress,udpport);
                    temp =  new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength()).trim();
                   if (DashboardActivity.getSInstance().gwconnection == 0) {
                       //ReveSensorEvent.getInstance().listener.onNewsUpdate(temp);
                   }
                    Log.d(TAG, "onUDP");
                    if (temp.contains("GatewayIP")) {
                        String reply = "Reveserver,Response,GatewayIP,192.168.1.104,192.168.1.104";
                        byte[] data = reply.getBytes();
                        DatagramPacket replyPacket = new DatagramPacket(data, data.length, IPAddress, udpport);
                        socket.send(replyPacket);
                    }*/
                }
            } catch (SocketException e) {
                e.printStackTrace();
                socket.close();
            } catch (IOException i) {
                i.printStackTrace();
                socket.close();
            }
        }
    }
}
