package com.reveautomation.revesmartsecuritykit_online.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.reveautomation.revesmartsecuritykit_online.ReveSensorEvent;
import com.reveautomation.revesmartsecuritykit_online.db.SessionManager;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class TcpServerService extends Service {

    private static final String TAG = "MqttMessageService";
    private String result;
    static TcpServerService Instance;
    SessionManager session;
    static ServerSocket ss2 = null;
    static int isrunning = 0,clientcount = 0;
    public ReveSensorEvent mHandler ;//= new ReveSensorEvent();
    Socket s = null;
    public HashMap tcpclients;

    static int server_port = 2323;
    public TcpServerService() {
    }
    public static TcpServerService getInstance() {
        return Instance;
    }
    @Override
    public void onCreate() {
        session = new SessionManager(TcpServerService.this);
        TcpServerService.Instance = this;
        tcpclients = new HashMap<String, Socket>();
        super.onCreate();
        Log.d(TAG, "onCreate");
        try {
            ss2 = new ServerSocket(server_port); // can also use static final
            // PORT_NUM
            // , when defined
            System.out.println("Server Listening......");
            //ss2.getInetAddress().getLocalHost();
        } catch (BindException e) {
            System.err.println("Already running.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server error");
        }
        //TerminalThread();
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (isrunning == 0) {
                    try {
                        s = ss2.accept();

                        ServerThread st = new ServerThread(s);
                        st.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Connection Error");
                    }
                }
                try {
                    ss2.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        thread.start();

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
        Intent intent = new Intent("android.intent.category.LAUNCHER");
        intent.setClassName("com.app.androidkt.mqtt", "com.app.androidkt.mqtt.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        sendBroadcast(new Intent("YouWillNeverKillMe"));
    }
}


class ServerThread extends Thread {
    String line = null;
    BufferedReader is = null;
    PrintWriter os = null;
    Socket s = null;
    String reader_name = "";
    int  isDisconnect = 0;

    public ServerThread(Socket s) {
        this.s = s;
    }
    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = new PrintWriter(s.getOutputStream());
            DashboardActivity.getSInstance().os = os;
            // server socket time out
            s.setSoTimeout(10000);
        } catch (IOException e) {
            System.out.println("IO error in server thread" + e.toString());
        }
        try {
            os.print("mname");
            os.flush();
            line = is.readLine();
            {
                reader_name = line;
                reader_name = reader_name+ TcpServerService.clientcount++;
                System.out.print("Reader name  " + reader_name);
                if (reader_name.contains("Reve_")) {
                    TcpServerService.getInstance().tcpclients.put(reader_name,os);
                   // Toast.makeText(DashboardActivity.getSInstance().getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    line = is.readLine().trim();
                    while (line.compareTo("QUITn") != 0 && isDisconnect == 0) {
                        if (line.equals("ping")){
                            os.print("ping");
                            os.flush();
                        }else {
                            ReveSensorEvent.getInstance().listener.onNewsUpdate(line);
                        }
                        line = is.readLine();
                        {
                            {

                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            line = this.getName(); // reused String line for getting thread name
            System.out.println("IO Error/ Client " + line + " terminated abruptly" + e.toString());
        } catch (NullPointerException e) {
            line = this.getName(); // reused String line for getting thread name
            System.out.println("Client " + line + " Closed" + e.toString());
        }
        finally {
            try {
                System.out.println("Connection Closing..");
                if (is != null) {
                    is.close();
                    System.out.println(" Socket Input Stream Closed");
                }
                if (os != null) {
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (s != null) {
                    s.close();
                    System.out.println("Socket Closed");
                }
            } catch (IOException ie) {
                System.out.println("Socket Close Error" + ie.toString());
            }
            if (TcpServerService.getInstance().tcpclients.containsKey(reader_name)) {
                TcpServerService.getInstance().tcpclients.remove(reader_name);
            }
        }// end finally
    }
}