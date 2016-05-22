package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/22/16.
 */
public class ConnectionBoolean {

    private static ConnectionBoolean active = null;

    public static boolean activeConnection = false;

    protected ConnectionBoolean(){}

    public static synchronized ConnectionBoolean getInstance(){
        if(null == active){
            active = new ConnectionBoolean();
        }
        return active;
    }
}
