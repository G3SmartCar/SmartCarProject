package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/18/16.
 */

/**
 * Used to check whether a bluetooth paired device was pressed, 
 * or whether the connect button under the Wifi Set IP textfield was pressed
 */

public class Properties {
    private static Properties active = null;

    public static boolean wifiStatus = false;

    protected Properties(){}
    
    public static synchronized Properties getInstance(){
        if(null == active){
            active = new Properties();
        }
        return active;
    }
}
