package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.platform.messages.chatMessage;

import java.io.*;
import java.net.Socket;

public class platformChannel extends Thread {
    // channel variables
    private Socket socket;
    private String lobby;
    private String gameQueue;
    private int gameID;

    // main server variable
    private PlatformCore serv;
    private ObjectOutputStream outServ;
    public  String ID;


    public platformChannel(Socket socket, PlatformCore serv) {
        //set socket and main server to the arguments
        this.socket = socket;
        this.serv = serv;
    }

    public void cliSend (Object o){
        try{
            outServ.writeObject(o);
            outServ.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Set up input and output streams for talking back and forth
            outServ = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in_socket = new ObjectInputStream(socket.getInputStream());

            serv.p1 = this;
            //out_socket.println("connected");

            // variables for parsing input
            Object message;

            // if the client is closed unexpectedly readline returns null so this catches any error
            while (!interrupted()) {
                // Read the client's message
                message = in_socket.readObject();

                switch (message) {
                    case PlayerMove p:
                        System.out.println("player action arrived");
                        serv.gameEvent(gameID, "TTT", p);
                        break;
                    case chatMessage cm:
                        if(serv.chatservers.contains(cm.getServ())){
                            serv.sendMult(cm);
                        }

                        // chat message has arrived from the chatroom
                        // PLACEHOLDER
                        // This switch would presumably be arguments passed to a chat function
                        // Update chat window ?
                        break;

                    default:
                        System.out.println("def "+message);
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
