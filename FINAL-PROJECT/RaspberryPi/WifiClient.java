import java.io.*;
import java.net.*;


public class WifiClient {
    public static void main(String[] args) throws IOException {
		
	if (args.length != 2) {
            System.err.println(
                "Usage: java WifiClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
			Socket cSocket=new Socket(hostName,portNumber);
			 PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(cSocket.getInputStream()));
        ) {
			 BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
			 String fromUser;
			 
			 while (true){
				 fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
				if (fromUser.equals("bye")) break;
			 }
		}
		catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
}}
