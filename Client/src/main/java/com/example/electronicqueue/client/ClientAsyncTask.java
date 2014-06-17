package com.example.electronicqueue.client;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronicqueue.server.MyBundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientAsyncTask extends AsyncTask<Activity, String, Void> implements Serializable {


    public static final String CONNECTION_TRUE = "Connection true!";
    public static final String GT = "gt"; //get ticket
    public static final String GTL = "gtl"; //get ticket list

    private String command = null;
    private TextView textViewConnection;
    private Button buttonGetTicket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Button buttonGetTicketList;
    private ListView listViewTicketLIst;
    private ArrayList<MyBundle> list;
    private AdapterListTicket adapter;
    private TextView textViewNumberTicket;
    private TextView textViewNumberWindow;
    private int numberTicket;
    private int numberWindow;
    private Activity activity;
    private String ipAddress;
    private int port;
    public Socket socket;


    @Override
    protected Void doInBackground(Activity... params) {

        activity = params[0];

//        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
//        int ip = wifiManager.getConnectionInfo().getIpAddress();
//        String ipString = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
//        Log.d(MainActivity.LOG_CLIENT, "My ip address: " + ipString);

        textViewConnection = (TextView) activity.findViewById(R.id.textViewConnection);
//        textViewConnection.setText(ipString);

        buttonGetTicket = (Button) activity.findViewById(R.id.buttonGetTicket);
        buttonGetTicketList = (Button) activity.findViewById(R.id.buttonGetTicketList);
        textViewNumberTicket = (TextView) activity.findViewById(R.id.textViewNumberTicket);
        textViewNumberWindow = (TextView) activity.findViewById(R.id.textViewNumberWindow);
        listViewTicketLIst = (ListView) activity.findViewById(R.id.listViewTicketList);


        try {

            getIpAddressAndPort();


            buttonGetTicket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objectOutputStream.writeUTF(GT);
                        objectOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    command = GT;
                }
            });


            buttonGetTicketList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objectOutputStream.writeUTF(GTL);
                        objectOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    command = GTL;
                }
            });

            connectAndReciveData();


        } catch (IOException e) {


//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e1) {
//                e1.printStackTrace();
//            }

//            connectAndReciveData();

        }


        return null;
    }

    private void connectAndReciveData() {
        try {
            socket = new Socket(ipAddress, port);

            Log.d(MainActivity.LOG_CLIENT, CONNECTION_TRUE);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewConnection.setText(CONNECTION_TRUE);
                }
            });


//            textViewConnection.post(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


            while (true) {

                Object object = objectInputStream.readObject();

                if (command.equals(GT)) {

                    MyBundle myBundle = (MyBundle) object;
                    numberWindow = myBundle.getNumberWindow();
                    numberTicket = myBundle.getNumberTicket();


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewNumberTicket.setText(String.valueOf(numberTicket));
                            textViewNumberWindow.setText(String.valueOf(numberWindow));
                        }
                    });

                    Log.d(MainActivity.LOG_CLIENT, "numberTicket: " + String.valueOf(numberTicket) + ", numberWindow: " + String.valueOf(numberWindow));
                    publishProgress("numberTicket: " + String.valueOf(numberTicket) + ", numberWindow: " + String.valueOf(numberWindow));


                } else if (command.equals(GTL)) {

                    list = (ArrayList<MyBundle>) object;

                    for (MyBundle myBundle : list) {
                        int numberWindow = myBundle.getNumberWindow();
                        int numberTicket = myBundle.getNumberTicket();
                        Log.d(MainActivity.LOG_CLIENT, "numberTicket: " + String.valueOf(numberTicket) + ", numberWindow: " + String.valueOf(numberWindow));
                    }

                    adapter = new AdapterListTicket(activity, list);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listViewTicketLIst.setAdapter(adapter);
                        }
                    });

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(activity.getString(R.string.errorConnectionToServer));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewConnection.setText(R.string.errorConnectionToServer);
                }
            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            connectAndReciveData();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getIpAddressAndPort() throws IOException {
        String filePath = "/" + Environment.DIRECTORY_DOWNLOADS + "/connection.txt";
        File file = new File(Environment.getExternalStorageDirectory(), filePath);
        if (file.exists()) {
            Log.d(MainActivity.LOG_CLIENT, "File connection exists true");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine(), ":");
            stringTokenizer.nextToken();
            ipAddress = stringTokenizer.nextToken();

            stringTokenizer = new StringTokenizer(bufferedReader.readLine(), ":");
            stringTokenizer.nextToken();
            port = Integer.valueOf(stringTokenizer.nextToken());


            Log.d(MainActivity.LOG_CLIENT, ipAddress + ":" + port);

        } else {
            Log.d(MainActivity.LOG_CLIENT, "File connection exists false");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append("IP_ADDRESS:192.168.1.100");
            fileWriter.append('\n');
            fileWriter.append("PORT:6666");
            fileWriter.flush();
            fileWriter.close();

            Log.d(MainActivity.LOG_CLIENT, "File create!");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Write in configure file", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(activity, values[0], Toast.LENGTH_SHORT).show();

    }
}
