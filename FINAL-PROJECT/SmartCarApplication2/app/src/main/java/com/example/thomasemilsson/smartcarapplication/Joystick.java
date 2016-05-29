package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by Axel on 14-Apr-16.
 *
 * @author         Axel Slättman
 * Camera       by Aras Bazyan
 * Bluetooth    by Thomas Emilsson
 * 09/05/2016
 *
 * Wifi         by Thomas Emilsson
 * Integration  Thomas/Axel
 * 20/05/2016
 * Version 1.3.2.1
 *
 * TODO: Add description to class and public methods (I can help here) in proper standard
 */


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class Joystick extends ConnectionActivity implements View.OnTouchListener, SensorEventListener {

    JoystickView v;
    Bitmap joy;
    Bitmap joybg;
    Bitmap cameraFeed;

    private static final String TAG = "Joystick";

    int zeroX, zeroY, car, speed;
    static long lastTime;
    float x, y, dx, dy, h, angle;

    boolean joySwitch = true;
    boolean sendData = true;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    Canvas c = new Canvas();
    Paint alpha = new Paint();


    ConnectionThread connectionThread;
    ConnectionHandler connectionHandler;

    // Handles Thread
    boolean connect = true;


    private String address, status, addressText;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joystick);


        lastTime = System.currentTimeMillis();


        // GET ADDRESS
        Intent newInt = getIntent();
        address = newInt.getStringExtra(ConnectionActivity.EXTRA_ADDRESS);

        // MAKE CONNECTION
        if (ConnectionBoolean.getInstance().activeConnection == false) {
            connectionHandler = new ConnectionHandler(connectionThread, address);
            ConnectionSingleton.getInstance().connectionHandler = connectionHandler;
            ConnectionBoolean.getInstance().activeConnection = true;
        }

        sendData = true;


        String feedSource = "http://" + IP.getInstance().activeIP + "/html/";
        WebView view = (WebView) this.findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:document.getElementById(\"mjpeg_dest\").click();");
                view.loadUrl("javascript:document.getElementById(\"mjpeg_dest\").removeAttribute(\"onclick\");");

            }
        });

        // SENSOR
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        view.setInitialScale(240);
        view.loadUrl(feedSource);

        v = new JoystickView(this);
        v.setOnTouchListener(this);
        joy = BitmapFactory.decodeResource(getResources(), R.drawable.joy1);
        joybg = BitmapFactory.decodeResource(getResources(), R.drawable.joybg);


        RelativeLayout surface = (RelativeLayout) findViewById(R.id.joystick);
        surface.addView(v);

        ConnectionSingleton.getInstance().connectionHandler.handleThread(address);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void switchControl(View view) {

        if (joySwitch) {
            v.stopPaint();
            v.pause();
            joySwitch = false;
            ImageButton btn = (ImageButton) findViewById(R.id.toTilt);
            btn.setBackgroundResource(R.drawable.joystick);
        } else {
            ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("m" + 0 + "\n");
            ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("t" + 0 + "\n");
            v.resume();
            v.stopPaint();
            joySwitch = true;
            ImageButton btn = (ImageButton) findViewById(R.id.toTilt);
            btn.setBackgroundResource(R.drawable.tilt2);
        }

    }


    @Override
    public void onBackPressed(){

        super.onBackPressed();

        sendData = false;

        if(joySwitch) {
            v.pause();
        }

        if(Properties.getInstance().wifiStatus) {
            ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("close\n");
            ConnectionBoolean.getInstance().activeConnection = false;
        }

        Properties.getInstance().wifiStatus = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ConnectionSingleton.getInstance().connectionHandler.connectionThread != null) {
            ConnectionSingleton.getInstance().connectionHandler.connectionThread.interrupt();
            ConnectionSingleton.getInstance().connectionHandler.connectionThread = null;
        }

        //v.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        v.resume();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!joySwitch) {
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

            if(sendData) {
                if (ConnectionSingleton.getInstance().connectionHandler.connected) {
                    ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("m" + speed + "\n");
                    ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("t" + angle + "\n");

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Joystick Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.thomasemilsson.smartcarapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Joystick Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.thomasemilsson.smartcarapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class JoystickView extends SurfaceView implements Runnable {

        Thread thread = null;
        SurfaceHolder holder;
        boolean check = false;
        float radius;
        int quadrant;

        boolean paint = true;


        public JoystickView(Context context) {
            super(context);
            holder = getHolder();
            setZOrderOnTop(true);

        }

        //Thread that repaints the canvas
        public void run() {

            while (check) {

                if (!holder.getSurface().isValid())
                    continue;



                    //Creating a color that is 100% transparent
                    alpha.setAlpha(100);

                    c = holder.lockCanvas();

                    //Making sure that the pixels can handle being transparent
                    holder.setFormat(PixelFormat.TRANSPARENT);

                    //Setting my transparent color as the background of the canvas
                    c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


                    //Drawing the joystick
                if (paint) {
                    c.drawBitmap(joybg, c.getWidth() / 2 - joybg.getWidth() / 2, c.getHeight() / 2 - joybg.getHeight() / 2, null);
                    radius = joybg.getWidth() / 2;

                    if (x == 0 && y == 0)
                        c.drawBitmap(joy, c.getWidth() / 2 - joy.getWidth() / 2, c.getHeight() / 2 - joy.getHeight() / 2, null);


                    else {
                        calc(x, y);
                        c.drawBitmap(joy, x - (joy.getWidth() / 2), y - (joy.getHeight() / 2), null);
                    }

                }
                    holder.unlockCanvasAndPost(c);

            }

        }

        public void pause() {
            check = false;
            while (check) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            thread = null;
        }

        public void resume() {
            check = true;
            thread = new Thread(this);
            thread.start();
        }

        public boolean stopPaint() {
            if (paint)
                paint = false;
            else
                paint = true;

            return paint;
        }

        //Limits the distance you can drag the joystick using math.
        public void calc(float xx, float yy) {
            zeroX = c.getWidth() / 2;
            zeroY = c.getHeight() / 2;

            dx = xx - zeroX;
            dy = yy - zeroY;

            //Pythagoras
            angle = (float) Math.atan(Math.abs(dy / dx));
            h = (float) Math.sqrt(dx * dx + dy * dy);
            if (h > 151) h = 151;
            speed = (int) ((h - 1) * 0.6666666667);


            if (dx > 0 && dy > 0) {
                if (h > radius) {  //Keep the joystick within limits
                    xx = (float) (zeroX + (radius * Math.cos(angle)));
                    yy = (float) (zeroY + (radius * Math.sin(angle)));
                }
                speed = -speed; //Reverse speed when pulling down on joystick
                quadrant = 1; //Assign quadrant so we can make sure the output gets correct later
            } else if (dx > 0 && dy < 0) {
                if (h > radius) {
                    xx = (float) (zeroX + (radius * Math.cos(angle)));
                    yy = (float) (zeroY - (radius * Math.sin(angle)));
                }
                quadrant = 0;
            } else if (dx < 0 && dy < 0) {
                if (h > radius) {
                    xx = (float) (zeroX - (radius * Math.cos(angle)));
                    yy = (float) (zeroY - (radius * Math.sin(angle)));
                }
                quadrant = 3;
            } else if (dx < 0 && dy > 0) {
                if (h > radius) {
                    xx = (float) (zeroX - (radius * Math.cos(angle)));
                    yy = (float) (zeroY + (radius * Math.sin(angle)));
                }
                speed = -speed;
                quadrant = 2;
            } else {
                xx = zeroX + dx;
                yy = zeroY + dy;
            }
            x = xx;
            y = yy;

            car = determineCarAngle(quadrant);


            if (System.currentTimeMillis() - lastTime > 300) {
                ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("m" + speed + "\n");
                ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("t" + car + "\n");
                lastTime = System.currentTimeMillis();
            }


        }

        //Function to convert angle from 0-90 scale into 0-360 based on which quadrant the joystick is in
        public int determineCarAngle(int q) {
            int a = 0;

            switch (q) {
                case 0:
                    a = (int) Math.abs((angle * 180 / Math.PI) - 90);
                    break;

                case 1:
                    a = (int) Math.abs((angle * 180 / Math.PI) - 90);
                    break;

                case 2:
                    a = (int) ((angle * 180 / Math.PI) - 90);
                    break;

                case 3:
                    a = (int) (angle * 180 / Math.PI) - 90;
                    break;
            }

            //Buffers to make joystick a bit more stable.
            if (a < 5 && a > -5)
                a = 0;
            if (a < 100 && a > 80) {
                a = 90;
                if (speed < 0)
                    speed = -speed;
            }
            if (a < -80 && a > -100) {
                a = -90;
                if (speed < 0)
                    speed = -speed;
            }

            return a;
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent me) {


        switch (me.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Get x and y
                //moving = true;

                x = me.getX();
                y = me.getY();
                break;
            case MotionEvent.ACTION_UP:
                //Reset every value on release of joystick
                x = y = dx = dy = h = angle = 0;
                car = speed = 0;
                //moving = false;
                ConnectionSingleton.getInstance().connectionHandler.connectionThread.sendData("m0\n");


                break;
            case MotionEvent.ACTION_MOVE:
                //Update x and y
                x = me.getX();
                y = me.getY();

        }


        return true;
    }


}
