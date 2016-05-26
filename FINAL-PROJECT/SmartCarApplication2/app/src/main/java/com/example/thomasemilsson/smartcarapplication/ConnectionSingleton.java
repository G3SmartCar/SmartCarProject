package com.example.thomasemilsson.smartcarapplication;

/**
 * Created by thomasemilsson on 5/22/16.
 */
public class ConnectionSingleton {

    private static ConnectionSingleton active = null;

    public static ConnectionHandler connectionHandler;

    protected ConnectionSingleton(ConnectionHandler connectionHandler){
        this.connectionHandler = connectionHandler;
    }

    public static synchronized ConnectionSingleton getInstance(){
        if(null == active){
            active = new ConnectionSingleton(connectionHandler);
        }
        return active;
    }
}
