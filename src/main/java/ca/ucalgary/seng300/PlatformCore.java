package ca.ucalgary.seng300;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class PlatformCore {
    // client id number
    private int clientNumber = 1;

    // Database
    public platformDB database;

    // list of clients that are connected but not logged in
    List<Integer> connected;

    // list of connections for users currently logged in
    List<platformChannel> users;

    public void cMsg (int i, String s){
        for(platformChannel h : users){
            if(h.ID == i){
                h.cSend(s);
            }
        }

    }


    // return the current client number and increase it by 1 for the next client
    public int getClientNumber() {
        return clientNumber++;
    }
    private int cActive = 0;

    public PlatformCore() throws IOException, InterruptedException {
        // Initialise database
        // PLACEHOLDER (including the names)
        database = new platformDB();
        database.updateorsmth("something");

        // Max clients
        int ClientMax = 3;
        // how many clients have connected in total
        int CCount = 0;
        // how many clients are active
        // list of threads
        users = new ArrayList<platformChannel>();

        // Create a server socket listening on port 2020
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 2020));
        // refresh if no new communications have arrived in 5 seconds
        serverSocket.setSoTimeout(5000);

        System.out.println("Server is listening");

        // wait for at least 1 client to connect
        Boolean wait = true;
        // Loop to listen for new clients
        do{
            // accept throws a timeout exception every 5 secs to check if there are no clients left
            try {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                wait = false;
                if(cActive < ClientMax) {
                    // Create a new thread to handle the connected client
                    users.add(CCount, new platformChannel(socket, this));
                    users.get(CCount).start();
                    cActive ++;
                    CCount++;
                }
                else {
                    socket.close();
                }
            }
            catch(Exception e) {
                // lets the while check happen instead of blocking on accept when it times out
            }
        }
        while(cActive != 0 || wait);
        // close the servers socket
        serverSocket.close();
        // join all of the threads if they exist
        for(Thread thread : users) {
            thread.join();
        }
        System.out.println("Server Closed");
    }


    public static void main(String[] args) {

        try {
            new PlatformCore(); // Start the server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
