package com.example.electronicqueue.operator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class OperatorAsyncTask extends AsyncTask<Activity, String, Void> {

    public static final String CONNECTION_TRUE = "Connection true!";
    public static final String OP = "op";       //operator
    private Activity activity;
    private String ipAddress;
    private int port;
    private Integer thisNumberWindow;
    private TextView textViewConnection;
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private int numberTicket;
    private TextView textViewNumberTicket;

    @Override
    protected Void doInBackground(Activity... params) {

        activity = params[0];

        Button buttonIamFree = (Button) activity.findViewById(R.id.buttonIamFree);
        textViewConnection = (TextView) activity.findViewById(R.id.textViewConnection);
        textViewNumberTicket = (TextView) activity.findViewById(R.id.textViewNumberTicket);

        try {

            getIpAddressAndPortAndNumberWindow();

            buttonIamFree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        objectOutputStream.writeInt(thisNumberWindow);
                        objectOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            connectAndReceiveData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void connectAndReceiveData() {
        try {
            socket = new Socket(ipAddress, port);

            Log.d(MainActivity.LOG_OPERATOR, CONNECTION_TRUE);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewConnection.setText(CONNECTION_TRUE);
                }
            });

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeUTF(OP);
            objectOutputStream.flush();


            while (true) {
                numberTicket = objectInputStream.readInt();
                textViewNumberTicket.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewNumberTicket.setText(String.valueOf(numberTicket));
                    }
                });
                Log.d(MainActivity.LOG_OPERATOR, "numberTicket: " + String.valueOf(numberTicket));
                publishProgress("numberTicket: " + String.valueOf(numberTicket));
            }

        } catch (IOException e) {
            e.printStackTrace();
            publishProgress(activity.getString(R.string.app_name) + ": " + activity.getString(R.string.errorConnectionToServer));
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
            connectAndReceiveData();                //reconnect
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void getIpAddressAndPortAndNumberWindow() throws IOException {
        String filePath = "/" + Environment.DIRECTORY_DOWNLOADS + "/connection.txt";
        File file = new File(Environment.getExternalStorageDirectory(), filePath);
        if (file.exists()) {
            Log.d(MainActivity.LOG_OPERATOR, "File connection exists true");

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            StringTokenizer stringTokenizer = new StringTokenizer(bufferedReader.readLine(), ":");
            stringTokenizer.nextToken();
            ipAddress = stringTokenizer.nextToken();

            stringTokenizer = new StringTokenizer(bufferedReader.readLine(), ":");
            stringTokenizer.nextToken();
            port = Integer.valueOf(stringTokenizer.nextToken());

            stringTokenizer = new StringTokenizer(bufferedReader.readLine(), ":");
            stringTokenizer.nextToken();
            thisNumberWindow = Integer.valueOf(stringTokenizer.nextToken());


            Log.d(MainActivity.LOG_OPERATOR, ipAddress + ":" + port + ", this number window: " + thisNumberWindow);

        } else {
            Log.d(MainActivity.LOG_OPERATOR, "File connection exists false");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.append("IP_ADDRESS:192.168.1.100");
            fileWriter.append('\n');
            fileWriter.append("PORT:6666");
            fileWriter.append('\n');
            fileWriter.append("NUMBER_WINDOW:1");
            fileWriter.flush();
            fileWriter.close();

            Log.d(MainActivity.LOG_OPERATOR, "File create!");
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
