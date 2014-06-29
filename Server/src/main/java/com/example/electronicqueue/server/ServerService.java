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

public class ServerService extends Service {


    public static final String GT = "gt"; //get ticket
    public static final String GTL = "gtl"; //get ticket list
    public static final String OP = "op";   //operator
    public static final String MON = "mon";   //monitor
    private int numberTicket = 1;

    private final Object mutex = new Object();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(MainActivity.LOG_SERVER, "Start server service");


        new Thread(new Runnable() {
            private ServerSocket serverSocket;
            private Socket socket;

            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(MainActivity.PORT);
                    while (true) {
                        socket = serverSocket.accept();
                        NewConnection newConnection = new NewConnection(socket);
                        newConnection.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        serverSocket.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class NewConnection extends Thread {

        Socket socket;
        DataBaseServerElectronicQueue dataBaseServerElectronicQueue = new DataBaseServerElectronicQueue(getApplicationContext());
        private SQLiteDatabase database = dataBaseServerElectronicQueue.getWritableDatabase();


        private NewConnection(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            Log.d(MainActivity.LOG_SERVER, "Connection true!");


            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());


                String command = objectInputStream.readUTF();
                Log.d(MainActivity.LOG_SERVER, "Get command " + command);


                if (command.equals(GT)) {
                    methodGT(objectOutputStream, objectInputStream);
                } else if (command.equals(OP)) {
                    methodOP(objectOutputStream, objectInputStream);
                } else if (command.equals(MON)) {
                    methodMON(objectOutputStream, objectInputStream);
                } else if (command.equals(GTL)) {
                    methodGTL(objectOutputStream);
                    //deprecated
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void methodGT(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException {
            while (true) {

                objectInputStream.readUTF();

                Integer numberTicketLocal;
                synchronized (mutex) {
                    numberTicketLocal = numberTicket++;
                    Log.d(MainActivity.LOG_SERVER, "generated numberTicket: " + String.valueOf(numberTicketLocal));

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET, numberTicketLocal);
                    database.insert(DataBaseServerElectronicQueue.NAME_TABLE, null, contentValues);
                }

                objectOutputStream.writeInt(numberTicketLocal);
                objectOutputStream.flush();
            }
        }


        private void methodOP(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException {
            while (true) {

                Integer numberWindow = objectInputStream.readInt();

                Integer numberTicket = null;
                Log.d(MainActivity.LOG_SERVER, "Get number window: " + String.valueOf(numberWindow));


                String selection = "window is null";
                synchronized (mutex) {
                    Cursor cursor = database.query(DataBaseServerElectronicQueue.NAME_TABLE, null, selection, null, null, null, DataBaseServerElectronicQueue._ID, "1");
                    /** запрос потом сменить, сортировать нужно не оп ID а по номеру билета */
                    int columnIndexId = cursor.getColumnIndex(DataBaseServerElectronicQueue._ID);
                    int columnIndexNumberTicket = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET);

                    int id = 0;
                    if (cursor.moveToFirst()) {
                        id = cursor.getInt(columnIndexId);
                        numberTicket = cursor.getInt(columnIndexNumberTicket);
                        Log.d(MainActivity.LOG_SERVER, "First empty numberTicket: " + String.valueOf(numberTicket) + ", id: " + id + ", add window: " + numberWindow);
                    }

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET, numberTicket);
                    contentValues.put(DataBaseServerElectronicQueue.COLUMN_WINDOW, numberWindow);
                    database.update(DataBaseServerElectronicQueue.NAME_TABLE, contentValues, "_id = " + String.valueOf(id), null);
                }

                if (numberTicket != null) {
                    objectOutputStream.writeInt(numberTicket);
                } else {
                    objectOutputStream.writeInt(-1);
                    Log.d(MainActivity.LOG_SERVER, "No empty numberTicket");
                }
                objectOutputStream.flush();

            }
        }


        private void methodMON(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) throws IOException {
            while (true) {

                objectInputStream.readUTF();

                synchronized (mutex) {
                    String selection = "window is null";
                    Cursor cursor = database.query(DataBaseServerElectronicQueue.NAME_TABLE, null, selection, null, null, null, DataBaseServerElectronicQueue._ID, "1");
                    /** запрос потом сменить, сортировать нужно не оп ID а по номеру билета */
                    int columnIndexId = cursor.getColumnIndex(DataBaseServerElectronicQueue._ID);
                    int columnIndexNumberTicket = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET);
                    int columnIndexWindow = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_WINDOW);


                    int id = 0;
                    if (cursor.moveToFirst()) {
                        id = cursor.getInt(columnIndexId);
                        Log.d(MainActivity.LOG_SERVER, "First empty numberTicket id: " + id);

                    }

                    int beginQuery = id - 6;
                    int endQuery = id + 5;

                    selection = "_id > " + beginQuery + " and _id < " + endQuery;
                    cursor = database.query(DataBaseServerElectronicQueue.NAME_TABLE, null, selection, null, null, null, DataBaseServerElectronicQueue._ID);

                    List<MyBundle> list = new ArrayList<MyBundle>();

                    if (cursor.moveToFirst()) {
                        do {
                            int numberTicket = cursor.getInt(columnIndexNumberTicket);
                            int numberWindow = cursor.getInt(columnIndexWindow);
                            id = cursor.getInt(columnIndexId);
                            MyBundle myBundle = new MyBundle(numberTicket, numberWindow);
                            Log.d(MainActivity.LOG_SERVER, "Send numberTicket: " + numberTicket + " and numberWindow: " + numberWindow + " its id: " + id);
                            list.add(myBundle);
                        } while (cursor.moveToNext());
                    }

                    objectOutputStream.writeObject(list);
                    objectOutputStream.flush();
                }
            }
        }

        @Deprecated
        private void methodGTL(ObjectOutputStream objectOutputStream) throws IOException {

            Cursor cursor = database.query(DataBaseServerElectronicQueue.NAME_TABLE, null, null, null, null, null, null);
            int columnIndexNumberTicket = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_NUMBER_TICKET);
            int columnIndexWindow = cursor.getColumnIndex(DataBaseServerElectronicQueue.COLUMN_WINDOW);

            List<MyBundle> list = new ArrayList<MyBundle>();

            if (cursor.moveToFirst()) {
                do {
                    int numberTicket = cursor.getInt(columnIndexNumberTicket);
                    int numberWindow = cursor.getInt(columnIndexWindow);
                    MyBundle myBundle = new MyBundle(numberTicket, numberWindow);
                    list.add(myBundle);
                } while (cursor.moveToNext());
            }


            objectOutputStream.writeObject(list);
            objectOutputStream.flush();
        }


    }


}
