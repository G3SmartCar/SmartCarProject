/*	Server code to run on Raspberry Pi. It will accept an incoming connection from the remote client and forward
any input through the serial port to the arduino. If the connection is broken server will need to be restarted.
 */
 
import java.net.*;
import java.io.*;

public class WifiServer{
        public static void main(String[] args) throws IOException{
        int portnumber=4444;		// Default port we use is 4444, unless a different one is specified when starting the server.
		boolean connected=false;	// We need to know if we're connected
        if (args.length==1) portnumber=Integer.parseInt(args[0]);	// find out if a different port is specified
		
		while(!connected){	// if we're not connected we listen.
		System.out.println("Not connected, waiting");
        try(
        ServerSocket serverSocket=new ServerSocket(portnumber);
        Socket clientSocket=serverSocket.accept();				// Accept incoming connection
        PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true); // Output stream if we want to send things back to client.
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Inputstream to listen to
        ){
        String inputLine;
        SerialOutput output = new SerialOutput();	// Create object to relay data to serial port.
        output.initialize();
        System.out.println("Connected");
		connected=true;		// If we get here a connection exists until the client closes it.
		
        while ((inputLine=in.readLine())!=null){	// If we receive data, pass it on to Arduino.
		if (inputLine.equals("close"){				// If client asks to close connection we do that
			serverSocket.close();
			output.close();
			connected=false;
		}
		else{
        output.sendSerial(inputLine.getBytes());
        }}}
        catch (IOException e){
        System.out.println("error");
		connected=false;	// connection broke so go back to listening for a new connection.
        }
        }}
}
