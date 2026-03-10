package ca.ucalgary.seng300;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class clientSock extends Thread{
    Boolean devmode = false;
    private Socket socket;
    // main server variable
    private ClientCore cliC;
    private int ID;
    public BufferedReader servRead;
    // username/user ID association table.
    public Map<Integer, String> chatID;

    public clientSock(ClientCore cc, Socket sock, BufferedReader red) {
        //set socket and main server to the arguments
        this.socket = sock;
        this.cliC = cc;
        this.servRead = red;
        this.ID = cliC.CID;
        chatID = new HashMap<>();
        chatID.put(cliC.CID, cliC.username);
    }

    public void msgRoute(String s) {
        // Full version

    }

    @Override
    public void run() {
        try {
            if (devmode) {
                System.out.println("Client socket for" + ID + " at " + socket.getInetAddress() + " is active");
            }
            String message;
            // wait for first client response
            // if the client is closed unexpectedly readline returns null so this catches any error
            while (!interrupted()) {
                // wait for an incoming message from the server
                try{
                    message = servRead.readLine();
                } catch (IOException e) {
                    //if(devmode){
                    //    System.out.println("timeout 10 seconds with no msg from server");
                    //}

                    continue;
                }

                if (devmode) {
                    System.out.println("Server Says: " + message);
                }
                // server instructions, whole message matches
                if (message.equals("exit")) {
                    break;
                }

                // communication string
                String[] parts = message.split("#");
                int val;

                switch (parts[0]) {
                    case "AUTH":
                        // response to login request, maybe in client core instead?
                        System.out.println("Login");
                        break;
                    case "GAME":
                        // response to login request, maybe in client core instead?
                        System.out.println("game");
                        break;
                    case "CHAT":
                        // chat message has arrived from the chatroom
                        val = Integer.parseInt(parts[2]);
                        // PLACEHOLDER
                        // This switch would presumably be arguments passed to a chat function
                        switch (parts[1]) {
                            case "JOIN":
                                // add the association for ID to the username
                                chatID.put(val, parts[3]);
                                System.out.println("CHAT: " + "User " + parts[3] + " has connected");
                                break;
                            case "QUIT":
                                // add the association for ID to the username
                                System.out.println("CHAT: " + "User " + chatID.get(val) + " has disconnected");
                                chatID.remove(val);
                                break;
                            case "FROM":
                                // Username : message
                                System.out.println("CHAT: " + chatID.get(val) + ": " + parts[3]);
                                break;
                        }
                        // Update chat window ?
                        break;
                }
            }
            // cleanup
            socket.close();
            System.out.println("Client " + ID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
