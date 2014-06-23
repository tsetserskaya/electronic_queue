package com.example.electronicqueue.monitor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;

import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import static com.example.electronicqueue.monitor.GetMonitorViewAsyncTask.IntentNumberTicket;
import static com.example.electronicqueue.monitor.GetMonitorViewAsyncTask.IntentNumberWindow;


public class MainActivity extends Activity {
    public static final String mylog = "mylog";
    public static final int BLUE = Color.BLUE;
    public NetworkInfo networkInfo;
    static TextView numberWindow, numberTicket, window1, window2, window3, window4, window5, ticket1, ticket2, ticket3, ticket4, ticket5, backgroundColor, prepare, prepare1, prepare2, prepare3;
    public boolean NETWORK_IS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numberWindow = (TextView) findViewById(R.id.dont_edit_window);
        numberTicket = (TextView) findViewById(R.id.dont_edit_operator);
        window1 = (TextView) findViewById(R.id.window1);
        window2 = (TextView) findViewById(R.id.window2);
        window3 = (TextView) findViewById(R.id.window3);
        window4 = (TextView) findViewById(R.id.window4);
        window5 = (TextView) findViewById(R.id.window5);

        ticket1 = (TextView) findViewById(R.id.ticketw1);
        ticket2 = (TextView) findViewById(R.id.ticketw2);
        ticket3 = (TextView) findViewById(R.id.ticketw3);
        ticket4 = (TextView) findViewById(R.id.ticketw4);
        ticket5 = (TextView) findViewById(R.id.ticketw5);

        backgroundColor = (TextView) findViewById(R.id.backgroundcolor);
        prepare = (TextView) findViewById(R.id.dont_edit_prepare);
        prepare1 = (TextView) findViewById(R.id.prepare1);
        prepare2 = (TextView) findViewById(R.id.prepare2);
        prepare3 = (TextView) findViewById(R.id.prepare3);

        backgroundColor.setBackgroundColor(BLUE);
        isOnline();
        monitorRefresh();
        try {


/////// переписать ////////




            while (NETWORK_IS == true) {
                Thread.sleep(5000);
                monitorRefresh();
                Log.d(mylog, " getMonitorViewAsyncTask.execute();");
            }
            if (NETWORK_IS==false) {
                Log.d(mylog, "No connection, end of work");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("refresh"));


    }



    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            int networkType = networkInfo.getType();
            if ((networkType == connectivityManager.TYPE_WIFI) || networkType == connectivityManager.TYPE_ETHERNET) {
                Log.d(mylog, "Network is");
                NETWORK_IS = true;
            }
        } catch (NullPointerException e) {
            NETWORK_IS = false;
            Log.d(mylog, "networkInfo.isNOTConnected()");
        }
        return true;
    }
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private void monitorRefresh() {
        Intent intent = new Intent(this, GetMonitorViewAsyncTask.class);
        startService(intent);
        Log.d(mylog, "Start Service");
    }

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNumberWindow = intent.getIntExtra(IntentNumberWindow,0);
            int receivedNumberTicket = intent.getIntExtra(IntentNumberTicket,0);
            switch (receivedNumberWindow) {
                case 1:
                    ticket1.setText(receivedNumberTicket);
                    break;
                case 2:
                    ticket2.setText(receivedNumberTicket);
                    break;
                case 3:
                    ticket3.setText(receivedNumberTicket);
                    break;
                case 4:
                    ticket4.setText(receivedNumberTicket);
                    break;
                case 5:
                    ticket5.setText(receivedNumberTicket);
                    break;
                case 0:
                    Log.d(mylog, "null intent value from AsyncTask");
                    break;
            }
        }
    };
}
