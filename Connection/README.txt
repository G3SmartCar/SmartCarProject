All connections classes necessary to allow both bluetooth and WIFI connections to function.


In the onCreate() method of activities, check whether a connection needs to be made.

   // MAKE CONNECTION
        if (ConnectionBoolean.getInstance().activeConnection == false) {
            connectionHandler = new ConnectionHandler(connectionThread, address);
            ConnectionSingleton.getInstance().connectionHandler = connectionHandler;
            ConnectionBoolean.getInstance().activeConnection = true;
        }
        

This way, the connection stays active even whilst switching activities, The connection is lost when disconnecting.
