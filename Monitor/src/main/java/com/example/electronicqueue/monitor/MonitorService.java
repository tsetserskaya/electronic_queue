package com.example.electronicqueue.monitor;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Олег on 23.06.2014.
 */
public class MonitorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        GetMonitorViewAsyncTask getMonitorViewAsyncTask = new GetMonitorViewAsyncTask();
        getMonitorViewAsyncTask.execute();

        return null;
    }
}
