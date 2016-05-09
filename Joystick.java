package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by Axel on 14-Apr-16.
 *
 * @author Axel Slättman
 * Camera by Aras Bazyan
 * Bluetooth by Thomas Emilsson
 * 09/05/2016
 *
 * Version 0.0.0.7
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class Joystick extends BluetoothActivity implements View.OnTouchListener {

    MyView v;
    Bitmap joy;
    Bitmap joybg;
    Bitmap cameraFeed;

    private static final String TAG = "Joystick";

    int zeroX, zeroY, car, speed;
    float x, y, dx, dy, h, angle;

    boolean go = false;

    Canvas c = new Canvas();
    Paint red = new Paint();
    Paint alpha = new Paint();

    BluetoothThread bluetoothThread;
    boolean connect = true;
    private String address, status, addressText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joystick);

        // GET ADDRESS
        Intent newInt = getIntent();
        address = newInt.getStringExtra(BluetoothActivity.EXTRA_ADDRESS);

        String feedSource = "http://172.20.10.6/html/";
        WebView view = (WebView) this.findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                // view.loadUrl("javascript:document.getElementsByClassName('container-fluid text-center').style.display = 'none'");
                view.loadUrl("javascript:document.getElementById(\"mjpeg_dest\").click();");

              /*  view.loadUrl("javascript:document.getElementById('toggle_display').style.display = 'none'");
                view.loadUrl("javascript:document.getElementById('main-buttons').style.display = 'none'");
                view.loadUrl("javascript:document.getElementById('secondary-buttons').style.display = 'none'");
                view.loadUrl("javascript:document.getElementsByClassName('navbar navbar-inverse navbar-fixed-top')[0].style.visibility='hidden'");
                view.loadUrl("javascript:document.getElementById('accordion').style.display = 'none'");
                view.loadUrl("javascript:document.getElementById('mjpeg_dest').onclick = null");*/

                //document.getElementById('righttbutton').onclick = null
            }
        });

        view.setInitialScale(240);
        view.loadUrl(feedSource);

        v = new MyView(this);
        v.setOnTouchListener(this);
        joy = BitmapFactory.decodeResource(getResources(), R.drawable.joy1);
        joybg = BitmapFactory.decodeResource(getResources(), R.drawable.joybg);


        RelativeLayout surface = (RelativeLayout) findViewById(R.id.joystick);
        surface.addView(v);

        if (connect)
            handleThread();
    }


    private void handleThread() {
        bluetoothThread = new BluetoothThread(address, new Handler() {

            @Override
            public void handleMessage(Message message) {
                String s = (String) message.obj;

                // Interpret Message
                if (s.equals("CONNECTED")) {
                    status = "CONNECTED! :)";
                    addressText = address;
                } else if (s.equals("CONNECTION FAILED")) {
                    status = "Connection Failed";
                    addressText = address;
                } else {
                    status = "help";
                }
            }
        });

        bluetoothThread.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        v.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        v.resume();
    }

    private void disconnect() {
        Log.v(TAG, "DISCONNECT button pressed");

        // STOP CAR IF DISCONNECTED
        bluetoothThread.sendData("m" + 0);
        bluetoothThread.sendData("t" + 0);

        if (bluetoothThread != null) {
            bluetoothThread.interrupt();
            bluetoothThread = null;
            Intent intent = new Intent(Joystick.this, BluetoothActivity.class);
            startActivity(intent);
            msg("Disconnecting");
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    public class MyView extends SurfaceView implements Runnable {

        Thread thread = null;
        SurfaceHolder holder;
        boolean check = false;
        float radius;
        int quadrant;

        String xText, yText, angleText, hypo, speedText, carText;



        public MyView(Context context) {
            super(context);
            holder = getHolder();
            setZOrderOnTop(true);
        }

        //Thread that repaints the canvas
        public void run() {
            while (check) {

                if (!holder.getSurface().isValid())
                    continue;

                red.setColor(Color.RED);
                red.setStyle(Paint.Style.STROKE);
                red.setStrokeWidth(3);
                red.setTextSize(30);
                alpha.setAlpha(100);

                c = holder.lockCanvas();

                holder.setFormat(PixelFormat.TRANSPARENT);

                c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                c.drawBitmap(joybg, c.getWidth() / 2 - joybg.getWidth() / 2, c.getHeight() / 2 - joybg.getHeight() / 2, null);
                radius = joybg.getWidth()/2;
                if(x == 0 && y == 0)
                    c.drawBitmap(joy, c.getWidth() / 2 - joy.getWidth() / 2, c.getHeight() / 2 - joy.getHeight() / 2, null);

                else {
                    calc(x, y);
                    c.drawBitmap(joy, x - (joy.getWidth()/2), y - (joy.getHeight()/2), alpha);
                }
               /* xText = "X = " + (int)dx;
                yText = "Y = " + (int)dy;
                angleText = "angle = " + (int)(angle*180/Math.PI);
                hypo = "Hypo = " + (int)h;
                speedText = "Speed = " + speed;
                carText = "Angle for car = " + car;
                c.drawText(xText,100,100,red);
                c.drawText(yText,100,150,red);
                c.drawText(angleText,100,200,red);
                c.drawText(hypo,100,250,red);
                c.drawText(speedText,100,300,red);
                c.drawText(carText,100,350,red);*/

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

        //Limits the distance you can drag the joystick using math.
        public void calc(float xx, float yy) {
            zeroX = c.getWidth() / 2;
            zeroY = c.getHeight() / 2;

            dx = xx - zeroX;
            dy = yy - zeroY;

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
                quadrant = 1; //Assign quadrant so we can transform the angle into correct one on 0-360 scale
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

            bluetoothThread.sendData("m" + speed + "\n");
            bluetoothThread.sendData("t" + car + "\n");
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
                x = me.getX();
                y = me.getY();
                break;
            case MotionEvent.ACTION_UP:
                //Reset every value on release of joystick
                x = y = dx = dy = h = angle = 0;
                car = speed = 0;
                bluetoothThread.sendData("m0\n");

                break;
            case MotionEvent.ACTION_MOVE:
                //Update x and y
                x = me.getX();
                y = me.getY();

        }


        return true;
    }


}


