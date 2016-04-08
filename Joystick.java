/**
 * @author Axel Slättman
 *06/04/2016

 * Version 0.0.0.3

 */


package com.gu.example.axel.joymeby;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;



public class Joystick extends Activity implements View.OnTouchListener {

    MyView v;
    Bitmap joy;
    Bitmap joybg;
    int zeroX, zeroY, car;
    float x, y, dx, dy, h, angle;
    Canvas c = new Canvas();
    Paint red = new Paint();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        v = new MyView(this);
        v.setOnTouchListener(this);
        joy = BitmapFactory.decodeResource(getResources(),R.drawable.joy1);
        joybg = BitmapFactory.decodeResource(getResources(),R.drawable.joybg);

        setContentView(v);
    }

    @Override
    protected void onPause(){
        super.onPause();
        v.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        v.resume();
    }





    public class MyView extends SurfaceView implements Runnable{

        Thread thread = null;
        SurfaceHolder holder;
        boolean check = false;
        float radius;
        int quadrant;
        String xText, yText, angleText, hypo, speed, carText;




        public MyView(Context context) {
            super(context);
            holder = getHolder();
        }

        //Thread that repaints the canvas
        public void run(){
            while(check){

                if(!holder.getSurface().isValid())
                    continue;

                red.setColor(Color.RED);
                red.setStyle(Paint.Style.STROKE);
                red.setStrokeWidth(3);
                red.setTextSize(30);

                c = holder.lockCanvas();
                c.drawARGB(255, 255, 255, 255);
                c.drawBitmap(joybg, c.getWidth() / 2 - joybg.getWidth() / 2, c.getHeight() / 2 - joybg.getHeight() / 2, null);
                radius = joybg.getWidth()/2;
                if(x == 0 && y == 0)
                    c.drawBitmap(joy, c.getWidth() / 2 - joy.getWidth() / 2, c.getHeight() / 2 - joy.getHeight() / 2, null);

                else {
                    calc(x, y);
                    c.drawBitmap(joy, x - (joy.getWidth()/2), y - (joy.getHeight()/2), null);
                }
                xText = "X = " + (int)dx;
                yText = "Y = " + (int)dy;
                angleText = "angle = " + (int)(angle*180/Math.PI);
                hypo = "Hypo = " + (int)h;
                speed = "Speed = " + (int)((h-1)*0.6666666667);
                carText = "Angle for car = " + car;
                c.drawText(xText,100,100,red);
                c.drawText(yText,100,150,red);
                c.drawText(angleText,100,200,red);
                c.drawText(hypo,100,250,red);
                c.drawText(speed,100,300,red);
                c.drawText(carText,100,350,red);

                holder.unlockCanvasAndPost(c);
            }
        }
        public void pause(){
            check = false;
            while(true){
                try{
                    thread.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }
            thread = null;
        }
        public void resume(){
            check = true;
            thread = new Thread(this);
            thread.start();
        }

        //Limits the distance you can drag the joystick using math.
        public void calc(float xx, float yy){
            zeroX = c.getWidth()/2;
            zeroY = c.getHeight()/2;

            dx = xx - zeroX;
            dy = yy - zeroY;

            angle = (float)Math.atan(Math.abs(dy/dx));
            h = (float)Math.sqrt(dx*dx+dy*dy);
            if(h>151) h = 151;

            if(dx > 0 && dy > 0) {
                if(h > radius) {
                    xx = (float) (zeroX + (radius * Math.cos(angle)));
                    yy = (float) (zeroY + (radius * Math.sin(angle)));
                }
                quadrant = 1;
            }
            else if(dx>0&&dy<0){
                if(h > radius) {
                    xx = (float) (zeroX + (radius * Math.cos(angle)));
                    yy = (float) (zeroY - (radius * Math.sin(angle)));
                }
                quadrant = 0;
            }
            else if(dx<0&&dy<0){
                if(h > radius) {
                    xx = (float) (zeroX - (radius * Math.cos(angle)));
                    yy = (float) (zeroY - (radius * Math.sin(angle)));
                }
                quadrant = 3;
            }
            else if(dx < 0 && dy > 0){
                if(h > radius) {
                    xx = (float) (zeroX - (radius * Math.cos(angle)));
                    yy = (float) (zeroY + (radius * Math.sin(angle)));
                }
                quadrant = 2;
            }

            else{
                xx = zeroX + dx;
                yy = zeroY + dy;
            }
            x = xx;
            y = yy;

            car = determineCarAngle(quadrant);
        }

        public int determineCarAngle(int q){
            int a = 0;

            switch (q){
                case 0: a = (int)Math.abs((angle*180/Math.PI)-90);
                    break;

                case 1: a = (int)(angle*180/Math.PI) + 90;
                    break;

                case 2: a = (int)Math.abs((angle*180/Math.PI)-270);
                    break;

                case 3: a = (int)(angle*180/Math.PI) +270;
                    break;
            }

            return a;
        }





    }
    @Override
    public boolean onTouch(View v, MotionEvent me) {


        switch(me.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = me.getX();
                y = me.getY();
                break;
            case MotionEvent.ACTION_UP:
                x = y = dx = dy = h = angle = 0;
                car = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                x = me.getX();
                y = me.getY();

        }


        return true;
    }


}
