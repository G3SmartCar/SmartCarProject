package com.example.thomasemilsson.smartcarapplication;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

/**
 * Created by thomasemilsson on 5/22/16.
 * ConnectionHandler class keeps track of a ConnectionThread instance and handles 
 * the thread. This way the methods do not have to be recreated in every new activity
 */

public class ConnectionHandler{

    ConnectionThread connectionThread;
    String address;
    Boolean connected = false;
    Handler writeHandler; 


    public ConnectionHandler(ConnectionThread connectionThread, String address){
        this.connectionThread = connectionThread;
        this.address = address;
    }

    public void handleThread(String address) {
        connectionThread = new ConnectionThread(address, new Handler() {

            @Override
            public void handleMessage(Message message) {
                String s = (String) message.obj;

                // Interpret Message
                if (s.equals("CONNECTED")) {
                    connected = true;

                } else if (s.equals("CONNECTION FAILED")) {
                    connected = false;

                } else {
                    connected = false;
                }
            }
        });

        //writeHandler = connectionThread.getWriteHandler();

        connectionThread.start();

    }

    public void disconnect(String TAG, Context from) {
        Log.v(TAG, "DISCONNECT button pressed");

        // STOP CAR IF DISCONNECTED
        connectionThread.sendData("m0" + "\n" + "t0" + "\n");

        if (connectionThread != null) {
            connectionThread.interrupt();
            connectionThread = null;

            Intent intent = new Intent(from, ConnectionActivity.class);
            intent.putExtra(ConnectionActivity.EXTRA_ADDRESS, "==");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessary to call from outside an Activity
            from.startActivity(intent);
        }

        // Disconnect, Set wifi to false
        Properties.getInstance().wifiStatus = false;
        ConnectionBoolean.getInstance().activeConnection = false;

        Intent intent = new Intent(from, ConnectionActivity.class);
        intent.putExtra(ConnectionActivity.EXTRA_ADDRESS, "==");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necessary to call from outside an Activity
        from.startActivity(intent);
        connectionThread.disconnect();
        connectionThread.stop();
    }

}
