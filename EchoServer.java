import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class EchoServer {

    private ConcurrentMap<String, AtomicLong> map = new ConcurrentHashMap<>();

    public void addWord(String word){
        AtomicLong l = map.get(word);
        if(l == null){
            l = new AtomicLong(1);
            l = map.putIfAbsent(word, l);
            if(l != null){
                l.incrementAndGet();
            }
        }else{
            l.incrementAndGet();
        }
    }

    public long getCount(String word){
        AtomicLong l = map.get(word);
        if(l != null){
            return l.longValue();
        }
        return 0;
    }



    public static void main(String[] args) {
        int port = 12321;
        EchoServer server = new EchoServer( port );
        server.startServer();
    }

    ServerSocket newEchoServer = null;
    Socket clientSocket = null;
    int numConnections = 0;
    int port;

    public EchoServer( int port ) {
        this.port = port;
    }

    public void startServer() {
        try {
            newEchoServer = new ServerSocket(port);
        }
        catch (IOException e) {
            System.out.println(e);
        }

    System.out.println("Server started.  Waiting for connections.");

    while ( true ) {
        try {
            clientSocket = newEchoServer.accept();
            numConnections++;
            EchoServerConnection connection = new EchoServerConnection (
                    clientSocket, numConnections, this);
            new Thread(connection).start();
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    }
}

class EchoServerConnection implements Runnable {
    BufferedReader input;
    PrintStream output;
    Socket clientSocket;
    int id;
    EchoServer server;

    public EchoServerConnection(Socket clientSocket, int id, EchoServer server) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.server = server;
        System.out.println("Connection "+ id +" established with : "
                + clientSocket);
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void run() {
        String line;

        try {

                while(true) {
                    line = input.readLine();
                    if(line != null){
                        server.addWord(line);
                        output.print("=>"+ server.getCount(line));
                        output.println();
                    }
                    else{
                        System.out.println( "Connection from client "+ id + " is closed.");
                        break;
                    }

                }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}