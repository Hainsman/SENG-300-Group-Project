package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.server.GameSessionRecord;
import ca.ucalgary.seng300.platform.server.ServerLobby;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.*;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.domain.User;

import java.net.*;
import java.io.*;
import java.util.*;
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
    public GameHistoryStore gameHistoryStore;
    public PlayerStatsStore playerStatsStore;
    public ParticipantStore participantStore;

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

    public synchronized boolean onHostLobby(String code, int gameId, platformChannel hostChannel, String clientPayload) {
        if (code == null || code.isBlank() || hostChannel == null) {
            return false;
        }
        if (lobbies == null) {
            lobbies = new HashMap<>();
        }
        String lobbyid = code.trim().toUpperCase();

        ServerLobby lob = new ServerLobby(lobbyid, gameId, hostChannel, clientPayload);
        if (lobbies.putIfAbsent(lobbyid, lob) != null) {
            return false;
        }
        System.out.println("Lobby opened: " + code + " (" + gameId + ")");
        return true;
    }

    public synchronized boolean onJoinLobby(String code, int requestedGameId, platformChannel guestChannel, String guestPayload) {
        if (code == null || code.isBlank() || guestChannel == null || lobbies == null) {
            return false;
        }
        String lobbyid = code.trim().toUpperCase();
        ServerLobby sl = lobbies.get(lobbyid);
        if (sl == null) {
            return false;
        }
        if (requestedGameId > 0 && requestedGameId != sl.getGameId()) {
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
        if (listOfUsers.isEmpty()) {
            listOfUsers = userStore.findByUserEmail(user);
        }
        int newSessionId = sessionHistoryStore.assignNewId();
        UUID UID = UUID.randomUUID();
        String incomingUsername = user;
        String incomingPassword = hashPassword(pass, HASH_ALGORITHM);

        if (listOfUsers.isEmpty()){
            return new sysMSG("AUTH", "FAIL", "Login failed, invalid email/username or password");
        }

        User matchedUser = listOfUsers.getFirst();
        boolean matchingIdentity = incomingUsername.equals(matchedUser.getlogin())
                || incomingUsername.equals(matchedUser.getEmail());
        if (matchingIdentity && incomingPassword.equals(matchedUser.getPasswordHash())){
            sessionHistoryStore.create(new SessionHistory(newSessionId, matchedUser.getID(), UID));
            return new sysMSG("AUTH", "PASS", matchedUser.getID()+"", matchedUser.getlogin());
        }
        return new sysMSG("AUTH", "FAIL", "Login failed, invalid email/username or password");
    }

    public sysMSG createAccount(String user, String pass, String email){
        int newSessionId = sessionHistoryStore.assignNewId();
        int newUserId = userStore.assignNewId();
        if(!userStore.findByUserName(user).isEmpty()){
            return new sysMSG("NEWU", "FAIL", "That username is already taken");
        }
        if(!isEmailValid(email, EMAIL_REGEX)){
            return new sysMSG("NEWU", "FAIL", "Please enter a valid email address");
        }
        sessionHistoryStore.create(new SessionHistory(newSessionId, newUserId, UUID.randomUUID()));
        userStore.create(new User(newUserId,user,email,hashPassword(pass,HASH_ALGORITHM)));
        return new sysMSG("NEWU", "PASS");
    }

    public void handleMove(String id, platformChannel player, PlayerAction m){
        if (id == null || lobbies == null) {
            return;
        }
        ServerLobby sl = lobbies.get(id);
        if (sl == null) {
            return;
        }
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
        userStore = UserStore.getInstance();
        sessionHistoryStore = SessionHistoryStore.getInstance();
        gameHistoryStore = GameHistoryStore.getInstance();
        playerStatsStore = PlayerStatsStore.getInstance();
        participantStore = ParticipantStore.getInstance();

        // list of threads
        users = new ArrayList<platformChannel>();
        connected = new ArrayList<Integer>();
        lobbies = new HashMap<>();

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

//    // chat list
//    public List<Integer> connected;
//    // list of chat/lobbies
//    public List<String> chatservers;
//
//    // hashmap of games gettable by ID
//    HashMap<String, Game> games;
//    // would be the list of all unused game ids, up to max size
//    String availableIDs = "test";
//
//    public platformChannel p1;
//    public platformChannel p2;
//
//    // list of connections for users currently logged in
//    public List<platformChannel> users;
//
//    private final Map<String, GameSessionRecord> sessions = new ConcurrentHashMap<>();
//
//    /** Waiting host channels keyed by lobby code (guest joins by code). */
//    private final ConcurrentHashMap<String, platformChannel> openLobbies = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, String> openLobbyGameTypes = new ConcurrentHashMap<>();
//
//    private static String normalizeGameType(String gameType) {
//        if (gameType == null) {
//            return "TTT";
//        }
//        String gt = gameType.trim().toUpperCase();
//        if (gt.contains("C4") || gt.contains("CONNECT")) {
//            return "C4";
//        }
//        return "TTT";
//    }
//
//    private static String gameIdForType(String gameType) {
//        return "C4".equals(gameType) ? "c4" : "test";
//    }
//
//    private static String playerIdForC4(String playerId) {
//        if ("P1".equals(playerId)) {
//            return Player.PLAYER_1.toString();
//        }
//        if ("P2".equals(playerId)) {
//            return Player.PLAYER_2.toString();
//        }
//        return playerId;
//    }
//
//    public synchronized boolean onHostLobby(String code, String gameType, platformChannel hostChannel) {
//        if (code == null || code.isBlank() || hostChannel == null) {
//            return false;
//        }
//        String normalized = code.trim().toUpperCase();
//        String normalizedType = normalizeGameType(gameType);
//        if (openLobbies.putIfAbsent(normalized, hostChannel) != null) {
//            return false;
//        }
//        openLobbyGameTypes.put(normalized, normalizedType);
//        hostChannel.ID = "host-" + normalized;
//        hostChannel.setGameType(normalizedType);
//        hostChannel.setGameID(gameIdForType(normalizedType));
//        System.out.println("Lobby opened: " + normalized + " (" + normalizedType + ")");
//        return true;
//    }
//
//    public synchronized boolean onJoinLobby(String code, platformChannel guestChannel) {
//        if (code == null || code.isBlank() || guestChannel == null) {
//            return false;
//        }
//        String normalized = code.trim().toUpperCase();
//        platformChannel host = openLobbies.remove(normalized);
//        String gt = openLobbyGameTypes.remove(normalized);
//        if (host == null) {
//            return false;
//        }
//        String gameType = normalizeGameType(gt != null ? gt : host.getGameType());
//        String gameId = gameIdForType(gameType);
//        host.setGameType(gameType);
//        host.setGameID(gameId);
//        guestChannel.setGameType(gameType);
//        guestChannel.setGameID(gameId);
//        guestChannel.ID = "guest-" + normalized;
//        p1 = host;
//        p2 = guestChannel;
//        if ("TTT".equals(gameType)) {
//            TTTGame t = (TTTGame) games.get("test");
//            Map<String, Player> turnIds = new HashMap<>();
//            turnIds.put("P1", Player.PLAYER_1);
//            turnIds.put("P2", Player.PLAYER_2);
//            t.setPlayerIdToPlayer(turnIds);
//        }
//        host.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("PARTNER_JOINED", normalized, ""));
//        tempGameReset();
//        System.out.println("Lobby " + normalized + " matched; game ready (" + gameType + ").");
//        return true;
//    }
//
//    /**
//     * Relays host "start game" to the guest using the same {@code GMJN} sysMSG pattern as the legacy client.
//     */
//    public synchronized void relayToGuestIfHost(platformChannel from, ca.ucalgary.seng300.platform.messages.sysMSG sm) {
//        if (p2 == null || from == null) {
//            return;
//        }
//        boolean fromHost = (from == p1)
//                || (from.ID != null && from.ID.startsWith("host-"));
//        if (!fromHost) {
//            return;
//        }
//        String fxml = sm.getFieldA();
//        if (fxml == null || fxml.isBlank()) {
//            return;
//        }
//        String b = sm.getFieldB() != null ? sm.getFieldB() : "";
//        p2.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("GMJN", fxml, b));
//        System.out.println("GMJN relayed to guest → " + fxml);
//    }
//
//    public void sendSingle (String id, Object o){
//        for(platformChannel u : users){
//            if(u.ID.equals(id)){
//                u.cliSend(o);
//            }
//        }
//    }
//
//    public void sendMult (chatMessage m){
//        for(platformChannel h : users){
//            h.cliSend(m);
//        }
//    }
//
//    public void clientJoin(int i){
//        connected.add(i);
//    }
//    public void clientQuit(int i){
//        connected.remove(i);
//    }
//
//    public void chatJoin(int i){
//        connected.add(i);
//    }
//    public void chatQuit(int i){
//        connected.remove(i);
//    }
//
//    // Called by matchmaking, creates a new game, adds both players
//    // then would remove players from matchmaking and notify them
//    public boolean gameCreate(String type, platformChannel p1, platformChannel p2){
//        return true;
//    }
//    public boolean tempJoin(platformChannel p){
//        if(p1 == null){
//            p1 = p;
//            return true;
//        }
//        else if(p2 == null){
//            p2 = p;
//            tempGameReset();
//            return true;
//        }
//        return false;
//    }
//    public void tempGameReset(){
//        String activeType = p1 != null ? normalizeGameType(p1.getGameType()) : "TTT";
//        if ("C4".equals(activeType)) {
//            C4Game c4 = (C4Game) games.get("c4");
//            c4.startGame(null, null);
//            if (p1 != null) {
//                p1.cliSend(c4.getGameState());
//            }
//            if (p2 != null) {
//                p2.cliSend(c4.getGameState());
//            }
//            return;
//        }
//        TTTGame t = (TTTGame) games.get("test");
//        t.tempReset();
//        if (p1 != null) {
//            p1.cliSend(t.getGameState());
//        }
//        if (p2 != null) {
//            p2.cliSend(t.getGameState());
//        }
//    }
//
//    public void gameEvent(String id, String type, PlayerMove p){
//        if (p1 == null || p2 == null) {
//            return;
//        }
//        String normalizedType = normalizeGameType(type);
//        if ("TTT".equals(normalizedType)) {
//            // Player channel stores the game type and ID for each client, no need to send it
//            TTTGame t =(TTTGame) games.get(id);
//
//            t.handleAction(new TTTPlayerAction(p.getPlayerID(),p.getActionType(), p.getRow(), p.getCol()) {
//            });
//            System.out.println(debugBoard((TTTGameState) t.getGameState()));
//            p1.cliSend(t.getGameState());
//            p2.cliSend(t.getGameState());
//            return;
//        }
//        if ("C4".equals(normalizedType)) {
//            C4Game c4 = (C4Game) games.get("c4");
//            c4.handleAction(new C4PlayerAction(playerIdForC4(p.getPlayerID()), p.getActionType(), p.getRow(), p.getCol()));
//            p1.cliSend(c4.getGameState());
//            p2.cliSend(c4.getGameState());
//        }
//    }
//    public String debugBoard(TTTGameState t) {
//        String s = "";
//        for (Player[] p : t.getBoard()) {
//            for (Player pp : p) {
//                s = s + pp;
//            }
//        }
//        return s;
//    }
//
//    // Number of active users
//    private int cActive = 0;
//
//    public PlatformCore() throws IOException, InterruptedException {
//
//        // Max clients -- PLACEHOLDER
//        int ClientMax = 6;
//        int CCount = 0;
//        games = new HashMap<>();
//        games.put("test", new TTTGame());
//        games.put("c4", new C4Game());
//
//        // list of threads
//        users = new ArrayList<platformChannel>();
//        connected = new ArrayList<Integer>();
//
//        // Create a server socket listening on port 2020
//        ServerSocket serverSocket = new ServerSocket();
//        serverSocket.bind(new InetSocketAddress("localhost", 2020));
//        // refresh if no new communications have arrived in 5 seconds
//        //serverSocket.setSoTimeout(5000);
//
//        System.out.println("Server is listening");
//
//        // wait for at least 1 client to connect
//        Boolean wait = true;
//        // Loop to listen for new clients
//        do{
//            // accept throws a timeout exception every 5 secs to check if there are no clients left
//            try {
//                // Accept a new client connection
//                Socket socket = serverSocket.accept();
//                wait = false;
//                if(cActive < ClientMax) {
//                    // Create a new thread to handle the connected client
//                    users.add(CCount, new platformChannel(socket, this));
//                    users.get(CCount).start();
//                    cActive ++;
//                    CCount++;
//                    System.out.println("client joined");
//                }
//                else {
//                    socket.close();
//                }
//            }
//            catch(Exception e) {
//                System.out.println("Server timeout");
//                // lets the while check happen instead of blocking on accept when it times out
//            }
//        }
//        while(cActive != 0 || wait);
//        // close the servers socket
//        serverSocket.close();
//        // join all of the threads if they exist
//        for(Thread thread : users) {
//            thread.join();
//        }
//        System.out.println("Server Closed");
//    }
//
//
//    public static void main(String[] args) {
//        try {
//            new PlatformCore(); // Start the server
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
