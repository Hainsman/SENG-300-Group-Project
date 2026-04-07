package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.server.GameSessionRecord;
import ca.ucalgary.seng300.platform.server.ServerLobby;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ca.ucalgary.seng300.platform.constants.Constants.*;
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
    //public HashMap<String, Game> games;

    // hashmap of joinable lobbies
    public HashMap<String, ServerLobby> lobbies;

    // list of connections for users currently logged in
    public List<platformChannel> users;

    private final Map<String, GameSessionRecord> sessions = new ConcurrentHashMap<>();

    public static String[] parseNameEloPayload(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[] { "Player", "1200" };
        }
        String[] parts = raw.split("\\|", 2);
        String name = parts[0].trim().isEmpty() ? "Player" : parts[0].trim();
        int elo = 1200;
        if (parts.length > 1) {
            try {
                elo = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException ignored) {
                elo = 1200;
            }
        }
        return new String[] { name, String.valueOf(elo) };
    }

    public synchronized boolean onHostLobby(String code, String gameType, platformChannel hostChannel, String clientPayload) {
        if (code == null || code.isBlank() || hostChannel == null) {
            return false;
        }
        String lobbyid = code.trim().toUpperCase();

        ServerLobby lob = new ServerLobby(lobbyid, gameType, hostChannel, clientPayload);
        if (lobbies.putIfAbsent(lobbyid, lob) != null) {
            return false;
        }
        System.out.println("Lobby opened: " + code + " (" + gameType + ")");
        return true;
    }

    public synchronized boolean onJoinLobby(String code, platformChannel guestChannel, String guestPayload) {
        if (code == null || code.isBlank() || guestChannel == null) {
            return false;
        }
        String lobbyid = code.trim().toUpperCase();
        ServerLobby sl = lobbies.get(lobbyid);
        if (sl == null) {
            return false;
        }
        sl.joinGuest(guestChannel, guestPayload);
        sl.rosterSend();
        sl.gameReset();
        System.out.println("Lobby " + code + " matched; game ready");
        return true;
    }

    public void sendMult (chatMessage m){
        for(platformChannel h : users){
            h.cliSend(m);
        }
    }

    public sysMSG authenticate(String user, String pass, platformChannel pl) {
        List<User> listOfUsers = userStore.findByUserName(user);
        int newSessionId = sessionHistoryStore.assignNewID();
        UUID UID = UUID.randomUUID();
        String incomingUsername = user;
        String incomingPassword = hashPassword(pass, HASH_ALGORITHM);

        if (listOfUsers.isEmpty()){
            return new sysMSG("AUTH", "FAIL", "Error Username not found");
        }

        if (incomingUsername.equals(listOfUsers.getFirst().getlogin()) && incomingPassword.equals(listOfUsers.getFirst().getPasswordHash())){
            sessionHistoryStore.create(new SessionHistory(newSessionId, listOfUsers.getFirst().getID(), UID));
            return new sysMSG("AUTH", "PASS", listOfUsers.getFirst().getID()+"",listOfUsers.getFirst().getlogin());
        }
        return new sysMSG("AUTH", "FAIL", "Login failed, invalid username or password");
    }

    public sysMSG createAccount(String user, String pass, String email){
        List<User> check = userStore.findByUserName(user);
        int newSessionId = sessionHistoryStore.assignNewID();
        UUID UID = UUID.randomUUID();
        int newUserId = userStore.assignNewID();
        if(!userStore.findByUserName(user).isEmpty()){
            return new sysMSG("NEWU", "FAIL", "That username is already taken");
        }
        if(!userStore.findByUserEmail(email).isEmpty()){
            return new sysMSG("NEWU", "FAIL", "That email is already registered");
        }
        if(!isEmailValid(email, EMAIL_REGEX)){
            return new sysMSG("NEWU", "FAIL", "Please enter a valid email address");
        }
        sessionHistoryStore.create(new SessionHistory(newSessionId, newUserId, UUID.randomUUID()));
        userStore.create(new User(newUserId,user,email,hashPassword(pass,HASH_ALGORITHM)));
        return new sysMSG("NEWU", "PASS");
    }

    public void handleMove(String id, platformChannel player, PlayerAction m){
        ServerLobby sl = lobbies.get(id);
        sl.gameTurn(player, m);
    }
    /**
     * Tic-Tac-Toe only: the forfeiting client loses and both receive the updated game state.
     */
    /*
    public synchronized void onForfeit(platformChannel from) {
        if (p1 == null || p2 == null || from == null) {
            return;
        }
        String normalizedType = normalizeGameType(p1.getGameType());
        if (!"TTT".equals(normalizedType)) {
            return;
        }
        Player forfeiting = null;
        if (from == p1) {
            forfeiting = Player.PLAYER_1;
        } else if (from == p2) {
            forfeiting = Player.PLAYER_2;
        }
        if (forfeiting == null) {
            return;
        }
        TTTGame t = (TTTGame) games.get("test");
        t.forfeit(forfeiting);
        System.out.println("Forfeit by " + forfeiting + " — broadcasting state");
        p1.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
        p2.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
    }*/

    // Number of active users
    private int cActive = 0;

    public PlatformCore() throws IOException, InterruptedException {
        /** database initialisation, replace null with the file path **/
        userStore = new UserStore(USER_FILE_PATH);
        sessionHistoryStore = new SessionHistoryStore(SESSION_FILE_PATH);

        // list of threads
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
        try {
            // Loop to listen for new clients
            do {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                wait = false;
                if (cActive < ClientMax) {
                    // Create a new thread to handle the connected client
                    users.add(CCount, new platformChannel(socket, this));
                    users.get(CCount).start();
                    cActive++;
                    CCount++;
                    System.out.println("client joined");
                } else {
                    socket.close();
                }
            }
            while (cActive != 0 || wait);
        }
        catch(Exception e) {
            System.out.println("Server timeout");
            // lets the while check happen instead of blocking on accept when it times out
        }
        finally {
            // close the servers socket
            serverSocket.close();
            // join all of the threads if they exist
            for(Thread thread : users) {
                thread.join();
            }
            System.out.println("Server Closed");
        }
    }


    public static void main(String[] args) {
        try {
            new PlatformCore(); // Start the server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
