package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/18/16.
 */

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by thomasemilsson on 4/13/16.
 */
public class ConnectionThread extends Thread {


    //Old Default Hosts:
    //String hostName = "192.168.43.140";
    //String hostName = "172.20.10.6";


    // Default port, always the same
    int portNumber = 4444;

    private static final char SPACER = '\n';
    private static final String TAG = "ConnectionThread";

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String address;

    private BluetoothSocket bluetoothSocket;

    protected OutputStream outStream;
    private InputStream inStream;

    private final Handler readHandler;
    private final Handler writeHandler;

    private String msgParser = "";

    public ConnectionThread(String address, Handler handler) {
        this.address = address.toUpperCase();
        this.readHandler = handler;

        writeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                write((String) message.obj);
            }
        };

    }

    // Getter for writeHandler
    public Handler getWriteHandler() {
        return writeHandler;
    }

    private void connect() throws Exception {
        Log.i(TAG, "Attempting connection to " + address + "...");

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if ((adapter == null) || (!adapter.isEnabled())) {
            throw new Exception("Bluetooth adapter not found or not enabled!");
        }

        BluetoothDevice myBluetooth = adapter.getRemoteDevice(address);

        // NO IDEA WHY THIS WORKS
        Method m = myBluetooth.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
        bluetoothSocket = (BluetoothSocket) m.invoke(myBluetooth, 1);

        //bluetoothSocket = myBluetooth.createInsecureRfcommSocketToServiceRecord(uuid);

        adapter.cancelDiscovery();
        bluetoothSocket.connect();

        outStream = bluetoothSocket.getOutputStream();
        inStream = bluetoothSocket.getInputStream();

        Log.i(TAG, "Connected successfully to " + address + ".");

    }

    private void disconnect() {
        Log.d("Disconnecting", "...");


        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (outStream != null) {
            try {
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (inStream != null) {
            try {
                inStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // Write data to the socket
    private void write(String s) {
        try {
            s += SPACER;
            // Convert to bytes and write
            outStream.write(s.getBytes());
            Log.i(TAG, "<Sent> " + s);
        } catch (Exception e) {
            Log.e(TAG, "Writing Failed!", e);
        }
    }


    // Run Method for Connections
    public void run() {
        Log.d(TAG, "" + Properties.getInstance().wifiStatus + " ======= ");

        try {
            // Check if wifi boolean is connected.
            if (Properties.getInstance().wifiStatus) {

                // Initialize Socket and change outStream
                Socket cSocket = new Socket(IP.getInstance().activeIP, portNumber);
                outStream = cSocket.getOutputStream();

                Log.d(TAG, "CONNECTED TO WIFI");

            } else {

                // Bluetooth connect() method
                connect();

            }

            sendToReadHandler("CONNECTED");


        } catch (Exception e) {
            Log.e(TAG, "Failed to connect!", e);
            sendToReadHandler("CONNECTION FAILED");
            disconnect();
            return;
        }

        if (Properties.getInstance().wifiStatus == false) {

            while (!this.isInterrupted()) {

                // Make sure things haven't gone wrong
                if ((inStream == null) || (outStream == null)) {
                    Log.e(TAG, "Lost bluetooth connection!");
                    break;
                }

                // Read data and add it to the buffer
                String s = read();
                if (s.length() > 0)
                    msgParser += s;

                // Look for complete messages
                parseMessages();
            }

            disconnect();
            sendToReadHandler("DISCONNECTED");
        }

    }

    // Unused currently, only writing Data
    private String read() {
        String s = "";

        try {
            if (inStream.available() > 0) {

                // Read bytes and move into buffer
                byte[] inBuffer = new byte[1024];
                int bytesRead = inStream.read(inBuffer);

                // Convert bytes into string
                s = new String(inBuffer, "ASCII");
                s = s.substring(0, bytesRead);
            }
        } catch (Exception e) {
            Log.e(TAG, "Read failed", e);
        }

        return s;
    }


    // Unused currently, only writing Data
    private void parseMessages() {
        int initial = msgParser.indexOf(SPACER);

        if (initial == -1)
            return;

        String s = msgParser.substring(0, initial);
        msgParser = msgParser.substring(initial + 1);
        sendToReadHandler(s);
        parseMessages();
    }

    private void sendToReadHandler(String s) {
        Message msg = Message.obtain();
        msg.obj = s;
        readHandler.sendMessage(msg);
        Log.i(TAG, "<RECEIVING> " + s);
    }

    // Sending Data: Works for both Wifi and Bluetooth sending
    public void sendData(String message) {
        // message += "\n";
        byte[] msgBuffer = message.getBytes();


        Log.d(TAG, "...Send data: " + message + "...");

        //Log.d(TAG, "...Send BUFFER: " + msgBuffer + "...");

        try {
            //printStream.println(message);
            outStream.write(msgBuffer);

        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();

             msg = msg + ".\n\nCheck that the SPP UUID: " + uuid.toString() + " exists on server.\n\n";
            msg = msg + ".\n\nOR Check that the host/port were entered correctly. \n\n";

        }
    }
}
