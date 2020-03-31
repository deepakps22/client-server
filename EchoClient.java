import java.io.*;
import java.net.*;

public class EchoClient {

    public static void main(String[] args) {

        String hostname = "localhost";
        int port = 12321;

        Socket clientSocket = null;
        DataOutputStream output = null;
        BufferedReader input = null;

        try {
            clientSocket = new Socket(hostname, port);
            output = new DataOutputStream(clientSocket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+ hostname);
        } catch (IOException e) {
            System.err.println("Couldn't establish I/O for the connection to "+ hostname);
        }

        if (clientSocket == null || output == null || input == null ) {
            System.err.println("Something has gone wrong.  Socket or I/O error.");
            return;
        }

        try {
            while (true) {
                System.out.print(">");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String keyInput = br.readLine();
                output.writeBytes(keyInput + "\n");

                String serverResponse = input.readLine();
                System.out.println(serverResponse);
            }
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: "+ e);
        } catch (IOException e) {
            System.err.println("IOException: "+ e);
        }
    }
}
