import java.net.*;
import java.io.*;

public class WifiServer{
        public static void main(String[] args) throws IOException{
        int portnumber=4444;
        if (args.length==1) portnumber=Integer.parseInt(args[0]);

        try(
        ServerSocket serverSocket=new ServerSocket(portnumber);
        Socket clientSocket=serverSocket.accept();
        PrintWriter out=new PrintWriter(clientSocket.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){
        String inputLine;
        SerialOutput output = new SerialOutput();
        output.initialize();
        byte[] initializing= {0,0};
        output.sendSerial(initializing);

        System.out.println("Connected");

        while ((inputLine=in.readLine())!=null){
        output.sendSerial(inputLine.getBytes());
        if (inputLine.equals("bye")) break;
        }}
        catch (IOException e){
        System.out.println(e.getMessage());
        }
        }
}
