package com.example.electronicqueue.client;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends Activity {

    public static final String LOG_CLIENT = "LogClient";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        String ipString = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        Log.d(MainActivity.LOG_CLIENT, "My ip address: " + ipString);


        ClientAsyncTask clientAsyncTask = new ClientAsyncTask();
        clientAsyncTask.execute(this);


    }


}
