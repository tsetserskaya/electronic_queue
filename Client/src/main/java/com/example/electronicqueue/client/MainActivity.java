package com.example.electronicqueue.client;

import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    public static final String LOG_CLIENT = "LogClient";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ClientAsyncTask clientAsyncTask = new ClientAsyncTask();
        clientAsyncTask.execute(this);

    }





}
