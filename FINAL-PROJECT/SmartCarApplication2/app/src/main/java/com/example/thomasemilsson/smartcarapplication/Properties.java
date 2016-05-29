package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/18/16.
 */

/**
 * TODO: Add description of this class
 */

public class Properties {
    private static Properties active = null;

    public static boolean wifiStatus = false;

    protected Properties(){}
    
    /**
     * TODO: Add method description
     *@return an instance of this class
     */
    

    public static synchronized Properties getInstance(){
        if(null == active){
            active = new Properties();
        }
        return active;
    }
}
