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
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class Tilt extends ConnectionActivity implements SensorEventListener {

    private static final String TAG = "Tilt";

    //ConnectionThread connectionThread;
    ConnectionThread connectionThread;
    //ConnectionHandler connectionHandler;
    //ConnectionSingleton connectionSingleton;


    Handler writeHandler; // not using

    private String address;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    int speed;
    int angle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.tilt);

        // GET ADDRESS
        Intent newInt = getIntent();
        address = newInt.getStringExtra(ConnectionActivity.EXTRA_ADDRESS);
        Log.d(TAG, "" + address + " =================");

        // SENSOR
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get the accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        // MAKE CONNECTION

           ConnectionSingleton.getInstance().connectionHandler = new ConnectionHandler(connectionThread, address);
            //ConnectionSingleton.getInstance().connectionHandler = connectionHandler;
            //ConnectionBoolean.getInstance().activeConnection = true;


        String feedSource = "http://" + IP.getInstance().activeIP + "/html";

        WebView view = (WebView) this.findViewById(R.id.tiltView);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:document.getElementById(\"mjpeg_dest\").click();");
                view.loadUrl("javascript:document.getElementById(\"mjpeg_dest\").removeAttribute(\"onclick\");");
            }
        });

        view.setInitialScale(240);
        view.loadUrl(feedSource);


        // TODO: A Way To Disconnect
        // TODO: Button For Switching to Joystick Control
        // OnClickListeners \\
//        disconnectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConnectionSingleton.getInstance().connectionHandler.disconnect(TAG, Tilt.this.getApplicationContext());
//            }
//        });

        // Handle Thread
        ConnectionSingleton.getInstance().connectionHandler.handleThread(address);
    }

    public void toJoy(View view)
    {
        Intent intent = new Intent(Tilt.this, Joystick.class);
        startActivity(intent);
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    // TILT CONTROL
    // TODO: Prevent Negative numbers Small than -100
    // TODO: Add Sensitivity Control
    public final void onSensorChanged(SensorEvent event) {
        float x = 0, y = 0;

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


        // TODO: Test this
//        if (speed <= -100){
//            speed = -100;
//        }


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

        if (ConnectionSingleton.getInstance().connectionHandler.connected) {

            ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("m" + speed + "\n");
            ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("t" + angle + "\n");

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

        if (ConnectionSingleton.getInstance().connectionHandler.connectionThread != null) {
            ConnectionSingleton.getInstance().connectionHandler.connectionThread.interrupt();
            ConnectionSingleton.getInstance().connectionHandler.connectionThread = null;
        }

        mSensorManager.unregisterListener(this);
    }
}
