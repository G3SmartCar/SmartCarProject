package com.example.thomasemilsson.smartcarapplication;

import android.content.Context;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class BasicActivity extends ConnectionActivity implements SensorEventListener {

    private static final String TAG = "BasicActivity";

    ConnectionThread connectionThread;

    Handler writeHandler;

    boolean connect = true;

    Button disconnectButton;
    TextView status;
    TextView textX, textY, textZ, textLeft, textRight;

    private String address;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    int speed;
    int angle;

    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        status = (TextView) findViewById(R.id.statusText);
        status.setText("waiting for connection");

        textX = (TextView) findViewById(R.id.textX);
        textY = (TextView) findViewById(R.id.textY);
        textZ = (TextView) findViewById(R.id.textZ);

        textX.setText("FUKCKFASKDLF");

        textLeft = (TextView) findViewById(R.id.textLeft);
        textRight = (TextView) findViewById(R.id.textRight);

        status.setText("Attempting to Connect...");

        // GET ADDRESS
        Intent newInt = getIntent();
        address = newInt.getStringExtra(ConnectionActivity.EXTRA_ADDRESS);
        Log.d(TAG, "" + address + " =================");


        // SENSOR \\
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get the accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // OnClickListeners \\

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        if (connect)
            handleThread();

    }

    private void disconnect() {
        Log.v(TAG, "DISCONNECT button pressed");

        // STOP CAR IF DISCONNECTED
        connectionThread.sendData("s");

        if (connectionThread != null) {
            connectionThread.interrupt();
            connectionThread = null;
            Intent intent = new Intent(BasicActivity.this, ConnectionActivity.class);
            intent.putExtra(EXTRA_ADDRESS, "==");
            startActivity(intent);
            msg("Disconnecting");
        }


        // Disconnect, Set wifi to false
        Properties.getInstance().wifiStatus = false;

        // TEST
        Intent intent = new Intent(BasicActivity.this, ConnectionActivity.class);
        intent.putExtra(EXTRA_ADDRESS, "==");
        startActivity(intent);
    }


    private void handleThread() {
        connectionThread = new ConnectionThread(address, new Handler() {

            @Override
            public void handleMessage(Message message) {
                String s = (String) message.obj;

                // Interpret Message
                if (s.equals("CONNECTED")) {
                    status.setText("Connected");
                    connected = true;

                } else if (s.equals("CONNECTION FAILED")) {
                    status.setText("Connection Failed");
                    connected = false;

                } else {
                    status.setText("help");
                    connected = false;
                }
            }
        });
        writeHandler = connectionThread.getWriteHandler();

        connectionThread.start();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    // TILT
    public final void onSensorChanged(SensorEvent event) {
        float x = 0, y = 0;//change to Int

        WindowManager windowMgr = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        int rotationIndex = windowMgr.getDefaultDisplay().getRotation();

        if (rotationIndex == 2 || rotationIndex == 4) {
            x = (int) event.values[1];
            y = (int) event.values[0];
        } else {
            x = (int) -event.values[0];
            y = (int) event.values[1];
        }

        // SOLVE SPEED \\
        int tempX = (int) Math.abs(x);
        int tempY = (int) Math.abs(y);

        // 15 is added for increased sensitivity
        if (tempX > tempY) {
            speed = ((int) x * 25);
        } else {
            speed = ((int) y * 25);
        }

        if (speed >= 100) {
            speed = 100;
        }
        if (speed <= -100){
            speed = -100;
        }


        // CHECKS FOR FULL RIGHT and FULL LEFT (90 Degrees)
        if (x >= 0) {
            speed = Math.abs(speed);
        }

        // SOLVE ANGLE \\
        // 25 added for increased sensitivity
        angle = (int) y * 25;

        if (angle > 90) {
            angle = 90;
        }

        if (angle < -90){
            angle = -90;
        }

        textX.setText("X axis" + "\t\t" + x);
        textY.setText("Y axis" + "\t\t" + y);

        if (connected) {

            connectionThread.sendData("m" + speed + "\n");
            connectionThread.sendData("t" + angle + "\n");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void message(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();

        if (connectionThread != null) {
            connectionThread.interrupt();
            connectionThread = null;
        }

        mSensorManager.unregisterListener(this);
    }
}
