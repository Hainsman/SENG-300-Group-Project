package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.server.GameSessionRecord;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponse;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ca.ucalgary.seng300.platform.constants.Constants.EMAIL_REGEX;
import static ca.ucalgary.seng300.platform.constants.Constants.HASH_ALGORITHM;
import static ca.ucalgary.seng300.platform.utils.Helper.hashPassword;
import static ca.ucalgary.seng300.platform.utils.Helper.isEmailValid;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class PlatformCore {
    // chat list
    public List<Integer> connected;
    // list of chat/lobbies
    public List<String> chatservers;

    /** Database setup**/
    public UserStore userStore;
    public SessionHistoryStore sessionHistoryStore;

    // hashmap of games gettable by ID
    HashMap<String, Game> games;
    // would be the list of all unused game ids, up to max size
    String availableIDs = "test";

    public platformChannel p1;
    public platformChannel p2;

    // list of connections for users currently logged in
    public List<platformChannel> users;

    private final Map<String, GameSessionRecord> sessions = new ConcurrentHashMap<>();

    /** Waiting host channels keyed by lobby code (guest joins by code). */
    private final ConcurrentHashMap<String, platformChannel> openLobbies = new ConcurrentHashMap<>();

    public synchronized boolean onHostLobby(String code, String gameType, platformChannel hostChannel) {
        if (code == null || code.isBlank() || hostChannel == null) {
            return false;
        }
        String normalized = code.trim().toUpperCase();
        if (openLobbies.putIfAbsent(normalized, hostChannel) != null) {
            return false;
        }
        hostChannel.ID = "host-" + normalized;
        System.out.println("Lobby opened: " + normalized + " (" + gameType + ")");
        return true;
    }

    public synchronized boolean onJoinLobby(String code, platformChannel guestChannel) {
        if (code == null || code.isBlank() || guestChannel == null) {
            return false;
        }
        String normalized = code.trim().toUpperCase();
        platformChannel host = openLobbies.remove(normalized);
        if (host == null) {
            return false;
        }
        guestChannel.ID = "guest-" + normalized;
        p1 = host;
        p2 = guestChannel;
        TTTGame t = (TTTGame) games.get("test");
        Map<String, Player> turnIds = new HashMap<>();
        turnIds.put("P1", Player.PLAYER_1);
        turnIds.put("P2", Player.PLAYER_2);
        t.setPlayerIdToPlayer(turnIds);
        host.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("PARTNER_JOINED", normalized, ""));
        tempGameReset();
        System.out.println("Lobby " + normalized + " matched; game ready.");
        return true;
    }

    /**
     * Relays host "start game" to the guest using the same {@code GMJN} sysMSG pattern as the legacy client.
     */
    public synchronized void relayToGuestIfHost(platformChannel from, ca.ucalgary.seng300.platform.messages.sysMSG sm) {
        if (p2 == null || from == null) {
            return;
        }
        boolean fromHost = (from == p1)
                || (from.ID != null && from.ID.startsWith("host-"));
        if (!fromHost) {
            return;
        }
        String fxml = sm.getFieldA();
        if (fxml == null || fxml.isBlank()) {
            return;
        }
        String b = sm.getFieldB() != null ? sm.getFieldB() : "";
        p2.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("GMJN", fxml, b));
        System.out.println("GMJN relayed to guest → " + fxml);
    }

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


    public String authenticate(String user, String pass, platformChannel pl){
        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
        List<User> listOfUsers= userStore.findByUserName(user);
        String incomingUsername = user;
        String incomingPassword = hashPassword(pass, HASH_ALGORITHM);

        return "N/A";

    }

    public String createAccount(String user, String pass, String email){
        List<User> check = userStore.findByUserName(user);
        if(!check.isEmpty()){
            //sysMSg username taken
        }
        if(!isEmailValid(email, EMAIL_REGEX)){
            //sysmsg invalid email
        }
        userStore.create(new User(1,user,email,hashPassword(pass,HASH_ALGORITHM)));
        return "done";
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
        if (p1 != null) {
            p1.cliSend(t.getGameState());
        }
        if (p2 != null) {
            p2.cliSend(t.getGameState());
        }
    }

    public void gameEvent(String id, String type, PlayerMove p){
        if (p1 == null || p2 == null) {
            return;
        }
        if(type.equals("TTT")){
            // Player channel stores the game type and ID for each client, no need to send it
            TTTGame t =(TTTGame) games.get(id);

            t.handleAction(new TTTPlayerAction(p.getPlayerID(),p.getActionType(), p.getRow(), p.getCol()) {
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
        /** database initialisation, replace null with the file path **/
        userStore = new UserStore(null);
        sessionHistoryStore = new SessionHistoryStore(null);

        // all of the lists of active threads
        games = new HashMap<>();
        users = new ArrayList<platformChannel>();
        connected = new ArrayList<Integer>();

        int ClientMax = 100;
        int CCount = 0;

        // Create a server socket listening on port 2020
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress("localhost", 2020));

        System.out.println("Server is listening");

        // wait for at least 1 client to connect
        Boolean wait = true;
        // Loop to listen for new clients
        do{
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
