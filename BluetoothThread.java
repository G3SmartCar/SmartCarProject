package com.example.thomasemilsson.smartcarapplication;

/**
 *
 * @author Thomas Emilsson
 * 13/04/2016
 *
 * Version 0.0.0.1
 */

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
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by thomasemilsson on 4/13/16.
 */
public class BluetoothThread extends Thread {

    private static final char SPACER = '\n';
    private static final String TAG = "BluetoothThread";

    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String address;

    private BluetoothSocket bluetoothSocket;

    private OutputStream outStream;
    private InputStream inStream;

    private final Handler readHandler;
    private final Handler writeHandler;

    private String msgParser = "";

    public BluetoothThread(String address, Handler handler) {
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
        Method m = myBluetooth.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
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

//    public void write(String message) {
//        Log.d(TAG, "...Data to send: " + message + "...");
//        byte[] msgBuffer = message.getBytes();
//        try {
//            outStream.write(msgBuffer);
//        } catch (IOException e) {
//            Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
//        }
//    }

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

    public void writeByte(byte[] bytes) {
        try {
            outStream.write(bytes);
        }
        catch (IOException e) {
            Log.e(TAG, "Error when writing to btOutputStream");
        }
    }

    public void run() {
        try {
            connect();
            sendToReadHandler("CONNECTED");
        } catch (Exception e) {
            Log.e(TAG, "Failed to connect!", e);
            sendToReadHandler("CONNECTION FAILED");
            disconnect();
            return;
        }

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

    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + uuid.toString() + " exists on server.\n\n";

        }
    }

}
