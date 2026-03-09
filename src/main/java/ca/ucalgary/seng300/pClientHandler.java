package ca.ucalgary.seng300;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    // socket variable
    private Socket socket;
    // main server variable
    private PlatformCore serv;

    public ClientHandler(Socket socket, PlatformCore serv) {
        //set socket and main server to the arguments
        this.socket = socket;
        this.serv = serv;
    }

    @Override
    public void run() {
        try {
            // get the id numebr from the main server
            int clientNumber = serv.getClientNumber();
            System.out.println("Client " + clientNumber + " at " + socket.getInetAddress() + " has connected.");
            // Set up input and output streams for talking back and forth
            BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // send the client id to the client which also tells it that the server accepted the client
            out_socket.println(clientNumber);
            // send the welcome to the client
            out_socket.println("Welcome to the Server Client " + clientNumber + "!");
            // wait for first client response
            String message = in_socket.readLine();
            System.out.println("Client " + clientNumber + " says: " + message);
            // if the client is closed unexpectedly readline returns null so this catches any error
            while (true) {
                // Read and print the client's message
                message = in_socket.readLine();
                // if the client sends exit, exit
                if (message.equals("exit")) {
                    break;
                } else {
                    System.out.println("Client " + clientNumber + " says: " + message);
                    // respond with client message + ACK
                    out_socket.println(message + " ACK");
                }
            }
            // cleanup
            socket.close();
            System.out.println("Client " + clientNumber + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
