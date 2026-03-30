package ca.ucalgary.seng300.platform.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    public Socket socket;
    public BufferedReader in;
    public PrintWriter out;
}
