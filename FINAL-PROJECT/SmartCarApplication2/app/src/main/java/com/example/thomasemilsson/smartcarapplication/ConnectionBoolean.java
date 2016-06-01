package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/22/16.
 */

/**
 * Class that returns an instance to determine whether wifi is connected.
 * Used to check whether Wifi or bluetooth connection is being made
 */

public class ConnectionBoolean {

    private static ConnectionBoolean active = null;

    public static boolean activeConnection = false;

    protected ConnectionBoolean(){}
    
    /**
     *@return an instance of this class
     */

    public static synchronized ConnectionBoolean getInstance(){
        if(null == active){
            active = new ConnectionBoolean();
        }
        return active;
    }
}
