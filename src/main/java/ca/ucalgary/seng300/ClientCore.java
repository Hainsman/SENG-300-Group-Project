package ca.ucalgary.seng300;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

/*
Documentation Stuff

The way a TCP connection works, you create a dedicated channel between two IP addresses
and then send stuff down it. The problem we have here is that the read function is a blocking
function in java, for keyboard, GUI(probably) and most importantly for net.Socket .

TCP is full duplex and is safe for multiple threads to be read/writing to it at once, but
best practice is to try and limit it to one read and write thread at a time.
ClientCore can send a message, Sock can receive a message.
PlatformCore has 2 types of listener thread and sends messages
pClientHandler listens to one specific client.

 */
public class ClientCore {
    public int CID;
    public String username;

    Boolean devmode = true;

    ClientCore() throws IOException {


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
            // show name
            CID = Integer.parseInt(message);
            if(devmode) {
                out_socket.println("This is Client " + CID);
                System.out.println("c1conf sent");
            }
            // receive server welcome message and show it
        }
        catch(Exception e){
            // if it times out trying to read the id the server closed the connection
            System.out.println("Server Closed Connection");
            socket.close();
            System.exit(0);
        }
        // set a 10 second interval to check if the client is being closed
        socket.setSoTimeout(10000);
        // Connection established, so hand everything off to the receiver Thread
        clientSock receiver = new clientSock(this, socket, in_socket);
        receiver.start();

        // Main loop
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
            // ALL DEBUG / PLACEHOLDER
            else if(message.equals("rl")) {
                out_socket.println("AUTH#Mjolnir Recon");
                out_socket.println("CHAT#JOIN#"+CID+"#Mjolnir Recon");
                //break;
            }
            else if(message.equals("rln")){
                int tval = 3 - CID;
                try{
                    out_socket.println("CHAT#"+tval+"#ihavebee}rolandbeowulfachil!esgilgameshiha");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#vebeencalleda[undrednamesandwillbecalledat");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#housandmorebeforetheworldgoesdimandcoldiam");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#%heroshehasbeenn~melesssinceourbi=thaconst");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#antadversarycaringfornothingbutmyruinaswor");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#ddrenchedinmybloodfor%vermygreatestand+nly");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#lovesheisthedarkoLetheenemyandloverwithout");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#whoZmyveryexistencewouldbepatheticandvulga");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#rourrelationshipiscom^lexandperhapseternal");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#wemetonceinthegardenatthebeginningofthewor");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#ldandunawareofourtwindestinieswematchedsta");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#resacrossad;yf\\untainandirecallhersmiling>");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#tmebeforeshedevouredthelawnandtreeswithatr");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#anslucentblueflameandtoreflagstonesfromthe");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#athandhurledthemintotheskyscreamingmysins");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#ipowderagranitemonumentinasoundlessflashsh");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#oweringthegrasswithmoltendropsofitsgoldinF");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#aysendingsmokingchipsofstoneskippingintoth");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#efogshesplinte!sanancientoakwitha/orcethat");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#takesmybreathandhurlsmetothegroundshelea%!");

                } catch (InterruptedException e) {
                    System.out.println("e");
                }
            }
            else if(message.equals("dr")) {
                out_socket.println("AUTH#Durandal");
                out_socket.println("CHAT#JOIN#"+CID+"#Durandal");
                //break;
            }
            else if(message.equals("drn")){
                int tval = 3 - CID;
                try{
                    out_socket.println("CHAT#"+tval+"#Find the right way down through the maze,");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#to the food, then find the exit.  Push the");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#exit button.   If the food tastes awful,");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#don't eat it, go back and try another way.");
                    Thread.sleep(4000);
                    out_socket.println("CHAT#"+tval+"#They want the same thing that you do,");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#really, they want a path, just like you.");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#You are in a maze in a maze, but which one");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#counts?  Your maze, their maze, my maze. ");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#Or are the mazes all the same, defined by ");
                    Thread.sleep(1000);
                    out_socket.println("CHAT#"+tval+"#the limits of their paths?");

                } catch (InterruptedException e) {
                    System.out.println("e");
                }
            }
        }
        receiver.interrupt();
        try{
            receiver.join();
        } catch (InterruptedException e) {
            System.out.println("thread killed");;
        }

        // Close the keyboard reader
        keyboard.close();
        // close the socket
        socket.close();
    }

    public static void main(String[] args) {

        try {
            new ClientCore(); // Start the client

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
