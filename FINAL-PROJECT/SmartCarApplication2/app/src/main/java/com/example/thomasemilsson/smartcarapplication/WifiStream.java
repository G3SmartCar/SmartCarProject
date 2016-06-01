package com.example.thomasemilsson.smartcarapplication;

import java.io.OutputStream;

/**
 * Created by thomasemilsson on 5/18/16.
 *  Used to store the type of outstream currently active
 *  Unused in our project, but important for future updates to project
 */
public class WifiStream {
    private static WifiStream active = null;

    public static OutputStream wifiStream;

    protected WifiStream(){}
    
    /**
     * @return an instance of this class
     */

    public static synchronized WifiStream getInstance(){
        if(null == active){
            active = new WifiStream();
        }
        return active;
    }
}
