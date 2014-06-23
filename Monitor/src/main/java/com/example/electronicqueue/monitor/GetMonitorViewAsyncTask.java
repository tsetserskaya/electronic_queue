package com.example.electronicqueue.monitor;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.server.MyBundle;

//import android.support.v4.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Олег on 23.06.2014.
 */
public class GetMonitorViewAsyncTask extends AsyncTask<Void, Void, Void> implements Serializable {

    public static final String IP = "10.0.0.107";
    public static final int PORT = 8080;
    public static final String IntentNumberWindow = "IntentNumberWindow";
    public static final String IntentNumberTicket = "IntentNumberWindow";
    String response = "";
    Socket socket;
    public static final String MON = "mon";
    public static int numberTicket;
    public static int numberWindow;
    public ObjectInputStream objectInputStream;
    public ObjectOutputStream objectOutputStream;
    public ArrayList<MyBundle> list;

    @Override
    protected Void doInBackground(Void... params) {
        InetAddress serverAddr = null;

        try {
            serverAddr = InetAddress.getByName(IP);
            socket = new Socket(serverAddr, PORT);
            Log.d(MainActivity.mylog, "socket is");


            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeUTF(MON);
            objectOutputStream.flush();
            Log.d(MainActivity.mylog, "print MON ");


            if (objectOutputStream != null) {
                Log.d(MainActivity.mylog, "OutputStream!=null");
            }


            objectInputStream = new ObjectInputStream(socket.getInputStream());

            while (true) {

                Object object = objectInputStream.readObject();
                list = (ArrayList<MyBundle>) object;

//                MyBundle myBundle = (MyBundle) object;
                for (MyBundle myBundle:  list) {

                    numberWindow = myBundle.getNumberWindow();
                    numberTicket = myBundle.getNumberTicket();
                }

                Intent intent = new Intent("refresh");
                intent.putExtra(IntentNumberWindow, numberWindow);
                intent.putExtra(IntentNumberTicket, numberTicket);



                


                ///////// тут ошибка////////////
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
//            Log.d(MainActivity.mylog, "after byteArrayOutputStream ");
//            byte[] buffer = new byte[1024];
//            Log.d(MainActivity.mylog, "after buffer ");
//
//
//            InputStream inputStream = socket.getInputStream();
//            Log.d(MainActivity.mylog, "inputStream");
//            if (inputStream != null) {
//                Log.d(MainActivity.mylog, "inputStream != null");
//            }
//            if (inputStream == null) {
//                Log.d(MainActivity.mylog, "inputStream == null");
//            }
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response = byteArrayOutputStream.toString();
//                String TOLOG_response = response;
//                Log.d(MainActivity.mylog, "response " + TOLOG_response);
//            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
//        } finally {
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
        return null;
//    } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (OptionalDataException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (StreamCorruptedException e) {
//            e.printStackTrace();
//        }
    }
}
