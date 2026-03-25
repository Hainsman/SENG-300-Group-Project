package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTActionPayload;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.server.GameSessionRecord;
import ca.ucalgary.seng300.platform.server.platformChannel;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class PlatformCore {
    // chat list
    public List<Integer> connected;
    // list of chat/lobbies
    public List<String> chatservers;

    // hashmap of games gettable by ID
    HashMap<String, Game> games;
    // would be the list of all unused game ids, up to max size
    String availableIDs = "test";

    public platformChannel p1;
    public platformChannel p2;

    // list of connections for users currently logged in
    public List<platformChannel> users;

    private final Map<String, GameSessionRecord> sessions = new ConcurrentHashMap<>();

    public void sendSingle (String id, Object o){
        for(platformChannel u : users){
            if(u.ID.equals(id)){
                u.cliSend(o);
            }
        }
    }

    public void sendMult (chatMessage m){
        for(platformChannel h : users){
            h.cliSend(m);
        }
    }

    public void clientJoin(int i){
        connected.add(i);
    }
    public void clientQuit(int i){
        connected.remove(i);
    }

    public void chatJoin(int i){
        connected.add(i);
    }
    public void chatQuit(int i){
        connected.remove(i);
    }

    // Called by matchmaking, creates a new game, adds both players
    // then would remove players from matchmaking and notify them
    public boolean gameCreate(String type, platformChannel p1, platformChannel p2){
        return true;
    }
    public boolean tempJoin(platformChannel p){
        if(p1 == null){
            p1 = p;
            return true;
        }
        else if(p2 == null){
            p2 = p;
            tempGameReset();
            return true;
        }
        return false;
    }
    public void tempGameReset(){
        TTTGame t =(TTTGame) games.get("test");
        t.tempReset();
        p1.cliSend(t.getGameState());
        p2.cliSend(t.getGameState());
    }

    public void gameEvent(String id, String type, PlayerMove p){
        //Game g = games.get(ID);
        if(type.equals("TTT")){
            // Player channel stores the game type and ID for each client, no need to send it
            TTTGame t =(TTTGame) games.get(id);

            t.handleAction(new TTTPlayerAction(p.getPlayerID(),p.getActionType(),new TTTActionPayload(p.getRow(),p.getCol())) {
            });
            System.out.println(debugBoard((TTTGameState) t.getGameState()));
            p1.cliSend(t.getGameState());
            p2.cliSend(t.getGameState());
        }
    }
    public String debugBoard(TTTGameState t) {
        String s = "";
        for (Player[] p : t.getBoard()) {
            for (Player pp : p) {
                s = s + pp;
            }
        }
        return s;
    }

    // Number of active users
    private int cActive = 0;

    public PlatformCore() throws IOException, InterruptedException {

        // Max clients -- PLACEHOLDER
        int ClientMax = 6;
        int CCount = 0;
        games = new HashMap<>();
        games.put("test", new TTTGame());

        // list of threads
        users = new ArrayList<platformChannel>();
        connected = new ArrayList<Integer>();

        // Create a server socket listening on port 2020
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 2020));
        // refresh if no new communications have arrived in 5 seconds
        //serverSocket.setSoTimeout(5000);

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
                    System.out.println("client joined");
                }
                else {
                    socket.close();
                }
            }
            catch(Exception e) {
                System.out.println("Server timeout");
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
