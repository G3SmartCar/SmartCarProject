package com.example.thomasemilsson.smartcarapplication;

import java.io.OutputStream;

/**
 * Created by thomasemilsson on 5/18/16.
 *  * TODO: Add description to class
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
