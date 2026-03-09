package ca.ucalgary.seng300;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class projectClient {

    projectClient() throws IOException {

        // Connect to the server at localhost on port 2020
        Socket socket = new Socket();
        try {
            // try to establish a connection with a 1 second timeout
            socket.connect(new InetSocketAddress("localhost", 2020), 1000);
            socket.setSoTimeout(1000);
        }
        catch (Exception e) {
            // if the timeout expires the server socket is closed so exit
            System.out.println("Client unable to connect, press enter to close");
            System.in.read();
            socket.close();
            System.exit(0);
        }

        // Set up input and output streams for talking back and forth
        BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        // read from the command line
        Scanner keyboard = new Scanner(System.in);

        String message;
        try {
            // read the id sent by the server
            message = in_socket.readLine();
            // if it reads an id the connection is valid so no more timeout needed
            socket.setSoTimeout(0);
            // show name
            out_socket.println("This is Client "+Integer.parseInt(message));
            // receive server welcome message and show it
            message = in_socket.readLine();
            System.out.println(message);
        }
        catch(Exception e){
            // if it times out trying to read the id the server closed the connection
            System.out.println("Server Closed Connection");
            socket.close();
            System.exit(0);
        }
        // list files
        while(true) {
            // Read CLI input
            message = keyboard.nextLine();
            // sent it to the server
            out_socket.println(message);
            // if the client sent exit, ignore server and close
            if(message.equals("exit")) {
                System.out.println("Closing connection");
                break;
            }
            message = in_socket.readLine();
            // if the server sends the start file flag
            System.out.println("Server says: "+ message);

        }
        // Close the keyboard reader
        keyboard.close();
        // close the socket
        socket.close();
    }

    public static void main(String[] args) {

        try {
            new projectClient(); // Start the client

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
