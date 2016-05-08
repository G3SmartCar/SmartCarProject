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

//
public class BasicActivity extends BluetoothActivity implements SensorEventListener {

    private static final String TAG = "BasicActivity";

    BluetoothThread bluetoothThread;

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

    int xAxis = 0;
    int yAxis = 0;
    int pwmMax = 255;
    int xR = 5;
    int xMax = 7;
    int yMax = 5;
    int yThreshold = 50;
    int motorLeft = 0;
    int motorRight = 0;
    String commandLeft = "L";        // command symbol for left motor from settings
    String commandRight = "R";

    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

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
        address = newInt.getStringExtra(BluetoothActivity.EXTRA_ADDRESS);


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
        bluetoothThread.sendData("s");

        if (bluetoothThread != null) {
            bluetoothThread.interrupt();
            bluetoothThread = null;
            Intent intent = new Intent(BasicActivity.this, BluetoothActivity.class);
            startActivity(intent);
            msg("Disconnecting");
        }
    }


    private void handleThread() {
        bluetoothThread = new BluetoothThread(address, new Handler() {

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
        writeHandler = bluetoothThread.getWriteHandler();

        bluetoothThread.start();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    // TILT
    public final void onSensorChanged(SensorEvent event) {
        float x = 0, y = 0;//change to Int
        String directionL = "";
        String directionR = "";
        String cmdSendL, cmdSendR;



        WindowManager windowMgr = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        int rotationIndex = windowMgr.getDefaultDisplay().getRotation();

        if (rotationIndex == 2 || rotationIndex == 4){
            x = (int) event.values[1];
            y = (int) event.values[0];
        } else {
            x = (int) -event.values[0];
            y = (int) event.values[1];
        }

        // SOLVE SPEED \\
        int tempX = (int) Math.abs(x);
        int tempY = (int) Math.abs(y);

        if (tempX > tempY) {
            speed = ((int) (x * 1.1111) * 10);
        } else {
            speed = ((int) (y * 1.1111) * 10);
        }

        if (speed > 100){
            speed = 100;
        }

        if (x >= 0){
            speed = Math.abs(speed);
        }

        // SOLVE ANGLE \\
        angle = (int) (float) Math.atan(Math.abs(y / x));

        if (x == 0) {
            angle = Math.abs((int) y * 10);
            if (y < 0) {
                angle = -angle;
            }

        } else if (x > 0 && y > 0){
            // do nothing
        } else if (x > 0 && y < 0){
            angle = -angle;
        } else if (x < 0 && y > 0){
            // do nothing
        } else if (x < 0 && y < 0){
            angle = -angle;
        } else{
            message("ERROR");
        }


//        int speed = ((int) (x * 1.1111) * 10);
//        int angle = (int) y * 10;
        textX.setText("X axis" + "\t\t" + x);
        textY.setText("Y axis" + "\t\t" + y);

        if (connected) {
            // message("CONNECTED?ASDFASDFASDF");
             bluetoothThread.sendData("m" + speed + "\n");
               bluetoothThread.sendData("t" + angle + "\n");
//            try {
//                Thread.sleep(50);                 //1000 milliseconds is one second.
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
        }
    }


//        if (rotationIndex == 1 || rotationIndex == 3) {            // detect 90 or 270 degree rotation (îïðåäåëÿåì ïîâîðîò óñòðîéñòâà íà 90 èëè 270 ãðàäóñîâ)
//            x = (int) -event.values[1];
//            y = (int) event.values[0];
//        } else {
//            x = (int) event.values[0];            //Force x axis in m s-2
//            y = (int) event.values[1];           //Force y axis in m s-2
//        }

//        // Many sensors return 3 values, one for each axis.
//        textX.setText("X axis" + "\t\t" + x);
//        textY.setText("Y axis" + "\t\t" + y);
//        //textZ.setText("Z axis" + "\t\t" + z);
//
//        xAxis = Math.round(x * pwmMax / xR);             //pwmMax = 255      xR = 5
//        yAxis = Math.round(y * pwmMax / yMax);           //yMax = 5
//        //both equal straight at this point
//        if (xAxis > pwmMax) xAxis = pwmMax;           // set max
//        else if (xAxis < -pwmMax) xAxis = -pwmMax;      //  and -max values
//        if (yAxis > pwmMax) yAxis = pwmMax;              //  dito
//        else if (yAxis < -pwmMax) yAxis = -pwmMax;        //  dito       negative - tilt forward
//        else if (yAxis >= 0 && yAxis < yThreshold)
//            yAxis = 0;    //Threshold=50  //roughly 20degrees before it moves
//        else if (yAxis < 0 && yAxis > -yThreshold) yAxis = 0;
//
//        if (xAxis > 0) {        // if tilt to left, slow down the left engine
//            motorRight = yAxis;
//            if (Math.abs(Math.round(x)) > xR) {       //xR = 5 is 45 degrees
//                motorLeft = Math.round((x - xR) * pwmMax / (xMax - xR));
//                motorLeft = Math.round(-motorLeft * yAxis / pwmMax);
//                //if(motorLeft < -pwmMax) motorLeft = -pwmMax;
//            } else motorLeft = yAxis - yAxis * xAxis / pwmMax; // tilt less than 45 degrees
//        } else if (xAxis < 0) {        // tilt to right
//            motorLeft = yAxis;
//            if (Math.abs(Math.round(x)) > xR) {
//                motorRight = Math.round((Math.abs(x) - xR) * pwmMax / (xMax - xR));
//                motorRight = Math.round(-motorRight * yAxis / pwmMax);
//                //if(motorRight > -pwmMax) motorRight = -pwmMax;
//            } else motorRight = yAxis - yAxis * Math.abs(xAxis) / pwmMax;
//        } else if (xAxis == 0) {       //Straight back/ forwards
//            motorLeft = yAxis;
//            motorRight = yAxis;
//        }
//
//        if (motorLeft > 0) {            // tilt to backward (íàêëîí íàçàä)
//            directionL = "-";
//        }
//        if (motorRight > 0) {        // tilt to backward (íàêëîí íàçàä)
//            directionR = "-";
//        }
//        motorLeft = Math.abs(motorLeft);
//        motorRight = Math.abs(motorRight);
//
//        if (motorLeft > pwmMax) motorLeft = pwmMax;
//        if (motorRight > pwmMax) motorRight = pwmMax;
//
//        cmdSendL = String.valueOf(commandLeft + directionL + motorLeft + ":");
//        cmdSendR = String.valueOf(commandRight + directionR + motorRight + ":");
//
//        textLeft.setText("cmdSendL" + "\t\t" + cmdSendL);
//        textRight.setText("cmdSendR" + "\t\t" + cmdSendR);
//

      //  int speed = ((int) (y * 1.1111) * 10);
      //  int angle = ((int) x * 10);

//        if (connected) {
//           // message("CONNECTED?ASDFASDFASDF");
//           // bluetoothThread.sendData("m" + speed + "\n");
//         //   bluetoothThread.sendData("t" + angle + "\n");
//            try {
//                Thread.sleep(50);                 //1000 milliseconds is one second.
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void message(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();

        if (bluetoothThread != null) {
            bluetoothThread.interrupt();
            bluetoothThread = null;
        }

        mSensorManager.unregisterListener(this);
    }
}
