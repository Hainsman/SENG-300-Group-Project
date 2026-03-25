package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class clientChannel extends Thread{
    // main server variable
    private ClientCore cliC;
    //private int ID;

    private ObjectInputStream servRead;
    private ObjectOutputStream servSend;

    public clientChannel(ClientCore cc) {
        //set socket and main server to the arguments
        this.cliC = cc;
    }

    public void msgSend(Object o) {
        try{
            servSend.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {

            // Connect to the server at localhost on port 2020
            Socket socket = new Socket();
            try {
                // try to establish a connection with a 1 second timeout
                socket.setSoTimeout(1000);
                socket.connect(new InetSocketAddress("localhost", 2020));
            }
            catch (Exception e) {
                // if the timeout expires the server socket is closed so exit
                System.out.println("Client unable to connect, press enter to close");
                //System.in.read();
                socket.close();
                System.exit(0);
            }

            // Set up input and output streams for talking back and forth
            servRead = new ObjectInputStream(new ObjectInputStream(socket.getInputStream()));
            servSend = new ObjectOutputStream(new ObjectOutputStream(socket.getOutputStream()));

            Object message;

            // set a 10 second interval to check if the client is being closed
            socket.setSoTimeout(10000);

            // wait for first client response
            // if the client is closed unexpectedly readline returns null so this catches any error
            while (!interrupted()) {
                // wait for an incoming message from the server
                try{
                    message = servRead.readObject();
                } catch (IOException e) {
                    continue;
                }

                // server instructions, whole message matches
                if (message.equals("exit")) {
                    break;
                }

                switch(message){
                    case sysMSG sm:
                        switch (sm.getType()){
                            case "LOGS":
                                cliC.username = sm.getFieldA();
                                cliC.CID = sm.getFieldB();
                                cliC.flg = 's';
                                cliC.upFlag = true;
                                break;
                            case "LOGF":
                                cliC.flg = 'f';
                                cliC.upFlag = true;
                                break;
                            case "GMJN":
                                cliC.upFlag = true;
                        }
                        break;


                    case GameStateUpdate g:
                        System.out.println("a gamestate arrived");
                        cliC.lastUpdate = g;
                        cliC.upFlag = true;
                        break;
                    case chatMessage c:
                        // CHAT SYSTEM UI COMPONENT
                        break;
                    //case matchMaking m:

                    default:
                        System.out.println("an object arrived");
                        break;
                }


            }
            // cleanup
            socket.close();
            System.out.println("Client " + cliC.CID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            cliC.flg = 'e';
            e.printStackTrace();
        }
    }
}


                /*
                // communication string
                String[] parts = message.split("#");
                int val;

                switch (parts[0]) {
                    // authorisation
                    case "AUTH":
                        // AUTH Y (ID) (username)
                        if(parts[1].equals("Y")){
                            ID = Integer.parseInt(parts[2]);
                            cliC.CID = ID;
                            cliC.username = parts[3];
                            //UI.DOTHING
                            //update UI state

                            // Get player stats for UI fields
                            servSend.println("QRDB#(a)");
                        }
                        else{
                            // LOGIN FAILED
                        }
                        break;
                    // answer to database query
                    case "QRDB":
                        switch (parts[1]){
                            // update a value based on the type of query it replies to
                            case "G1ST":
                                //cliC.tttStats = parts[2];
                                break;
                            case "G2ST":
                                //cliC.bjStats = parts[2];
                                break;
                            case "misc":

                                break;
                        }
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
                 */