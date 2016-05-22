package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/20/16.
 */
public class IP {
    private static IP active = null;

    public static String activeIP = "";

    protected IP(){}

    public static synchronized IP getInstance(){
        if(null == active){
            active = new IP();
        }
        return active;
    }
}
