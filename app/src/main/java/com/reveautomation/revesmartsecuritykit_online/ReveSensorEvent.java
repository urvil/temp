package com.reveautomation.revesmartsecuritykit_online;


/**
 * Created by Reve on 12/15/2016.
 */

public class ReveSensorEvent   {


    public ReveSensorEventListener listener;


    static ReveSensorEvent instance ;//= new ReveSensorEvent();
    public static ReveSensorEvent getInstance() {
        return instance;
    }

    public ReveSensorEvent() {
        // set null or default listener or accept as argument to constructor
        this.listener = null;
        ReveSensorEvent.instance = this;
    }

    // Assign the listener implementing events interface that will receive the events

    public void setOnNewsUpdateListener(ReveSensorEventListener listener) {
        this.listener = listener;
       // loadDataAsync();
    }





    /*ReveSensorEventListener listeners = new ReveSensorEventListener() {
        @Override
        public void onNewsUpdate(String data) {
         return;
        }
    };
    public void setOnNewsUpdateListener (ReveSensorEventListener listener)
    {
        // Store the listener object
        this.listeners = listener;

    }

*/



   /* ReveSensorEventListener myHandler;
    public void setHandlerListener(ReveSensorEventListener listener)
    {
        myHandler=listener;
    }
    protected void myEventFired(String data)
    {
        if(myHandler!=null)
            myHandler.onHandle(data);
    }*/



}
