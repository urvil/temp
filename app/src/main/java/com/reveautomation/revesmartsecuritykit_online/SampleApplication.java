package com.reveautomation.revesmartsecuritykit_online;

import android.app.Application;
import android.content.Context;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

public class SampleApplication extends Application {

    private RxBleClient rxBleClient;

    /**
     * In practise you will use some kind of dependency injection pattern.
     */
    public static RxBleClient getRxBleClient(Context context) {
        SampleApplication application = (SampleApplication) context.getApplicationContext();
        return application.rxBleClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            rxBleClient = RxBleClient.create(this);
            RxBleClient.setLogLevel(RxBleLog.DEBUG);
        }catch (Exception e) {
        }


    }
}
