package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/20/16.
 */

/**
 * Class to return an instance of the IP. For the demo the IP is provided in the code.
 * After demo, the user will enter the IP manually, according to the IP of his/her 
 * hotspot or other Wifi source
 */

public class IP {
    private static IP active = null;
    

    public static String activeIP = "172.20.10.6";
    
    // TODO: Add an intent containing the information of the IP recieved in
    // ConnectionActivity.java from the user. Comment that out for the demo.

    protected IP(){}
    
    /**
     * Add short method description. 
     * @return an instance of this class 
     */

    public static synchronized IP getInstance(){
        if(null == active){
            active = new IP();
        }
        return active;
    }
}
