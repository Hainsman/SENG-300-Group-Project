package ca.ucalgary.seng300;
import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    // socket variable
    private Socket socket;
    // main server variable
    private PlatformCore serv;
    public PrintWriter outServ;
    public  Integer ID;


    public ClientHandler(Socket socket, PlatformCore serv) {
        //set socket and main server to the arguments
        this.socket = socket;
        this.serv = serv;
    }

    public void cSend (String s){
        outServ.println(s);
    }

    @Override
    public void run() {
        try {
            // get the id numebr from the main server
            int clientNumber = serv.getClientNumber();
            ID = clientNumber;
            System.out.println("Client " + clientNumber + " at " + socket.getInetAddress() + " has connected.");
            // Set up input and output streams for talking back and forth
            BufferedReader in_socket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            outServ = out_socket;
            // send the client id to the client which also tells it that the server accepted the client
            out_socket.println(clientNumber);
            // send the welcome to the client
            out_socket.println("Welcome to the Server Client " + clientNumber + "!");
            // wait for first client response
            String message;
            String[] parts;
            int val;
            //System.out.println("Client " + clientNumber + " says: " + message);
            // if the client is closed unexpectedly readline returns null so this catches any error
            while (!interrupted()) {
                // Read and print the client's message
                message = in_socket.readLine();
                //try {
                    //System.out.println("rec "+message);
                    parts = message.split("#");
                    //System.out.println("parts "+parts[0]);

                //} catch (Exception e) {
                    //parts = new String[] {"err"};
                    //System.out.println("oops");
                //}

                switch (parts[0]) {
                    case "exit":
                        this.interrupt();
                        break;
                    case "AUTH":
                        if(serv.database.authenticateorsmth(parts[1])){
                            this.cSend("yes");
                        }
                        else{
                            this.cSend("no");
                        }
                        break;
                    case "CHAT":
                        if(parts[1].equals("JOIN")){
                            val = Integer.parseInt(parts[2]);
                            serv.cMsg(3-val, "CHAT#JOIN#"+val+"#"+parts[3]);
                        }
                        else{
                            val = Integer.parseInt(parts[1]);
                            serv.cMsg(val, "CHAT#FROM#"+ID+"#"+parts[2]);
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
            System.out.println("Client " + clientNumber + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
