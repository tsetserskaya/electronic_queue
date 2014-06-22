package com.example.electronicqueue.server;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerService extends Service {


    public static final String GT = "gt"; //get ticket
    public static final String GTL = "gtl"; //get ticket list
    //    public Socket socket;
    private int numberTicket = 1;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    //    private ServerSocket serverSocket;
    private SQLiteDatabase database;
//    public Socket socket;
//    public ServerSocket serverSocket;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(MainActivity.LOG_SERVER, "Start server service");


        new Thread(new Runnable() {

//            Socket socket;

            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(MainActivity.PORT);
                    while (true) {
                        Socket socket = serverSocket.accept();
                        Test test = new Test(socket);
                        test.start();
                    }
//                    } finally {
////                        socket.close();
////                        serverSocket.close();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class Test extends Thread {

        Socket socket;

        private Test(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            Log.d(MainActivity.LOG_SERVER, "Connection true!");

            DataBaseServerElectronicQueue dataBaseServerElectronicQueue = new DataBaseServerElectronicQueue(getApplicationContext());
            database = dataBaseServerElectronicQueue.getWritableDatabase();

            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());


                while (true) {

                    String command = objectInputStream.readUTF();
                    Log.d(MainActivity.LOG_SERVER, "Get command " + command);


                    if (command.equals(GT)) {

                        int numberTicket = ServerService.this.numberTicket++;
                        int numberWindow = new Random().nextInt(5) + 1;
                        Log.d(MainActivity.LOG_SERVER, "numberTicket: " + String.valueOf(numberTicket) + ", numberWindow: " + String.valueOf(numberWindow));


                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET, numberTicket);
                        contentValues.put(DataBaseServerElectronicQueue.COLUMN_WINDOW, numberWindow);
                        database.insert(DataBaseServerElectronicQueue.NAME_TABLE, null, contentValues);

                        MyBundle myBundle = new MyBundle(numberTicket, numberWindow);
                        objectOutputStream.writeObject(myBundle);
                        objectOutputStream.flush();
                    } else if (command.equals(GTL)) {

                        Cursor cursor = database.query(DataBaseServerElectronicQueue.NAME_TABLE, null, null, null, null, null, null);
                        int columnIndexPosition = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET);
                        int columnIndexWindow = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_WINDOW);

                        List<MyBundle> list = new ArrayList<MyBundle>();

                        if (cursor.moveToFirst()) {
                            do {
                                int numberTicket = cursor.getInt(columnIndexPosition);
                                int numberWindow = cursor.getInt(columnIndexWindow);
                                MyBundle myBundle = new MyBundle(numberTicket, numberWindow);
                                list.add(myBundle);
                            } while (cursor.moveToNext());
                        }


                        objectOutputStream.writeObject(list);
                        objectOutputStream.flush();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
