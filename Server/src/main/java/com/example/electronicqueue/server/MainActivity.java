package com.example.electronicqueue.server;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends Activity {

    public static final String LOG_SERVER = "LogServer";
    public static final int PORT = 6666;

    private TextView textViewIpAddressAndPort;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ip = wifiManager.getConnectionInfo().getIpAddress();
        String ipString = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        Log.d(LOG_SERVER, ipString + ":" + String.valueOf(PORT));


        textViewIpAddressAndPort = (TextView) findViewById(R.id.textViewIpAddressAndPort);
        textViewIpAddressAndPort.setText(ipString + ":" + String.valueOf(PORT));


        startService(new Intent(this, ServerService.class));


    }


}
