package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.constants.Rank;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.GameStateNetworkCodec;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    /** Waiting host channels keyed by lobby code (guest joins by code). */
    private final ConcurrentHashMap<String, platformChannel> openLobbies = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> openLobbyGameTypes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> openLobbyHostPayload = new ConcurrentHashMap<>();

    private static final int MAX_LOBBY_RANK_GAP = 1; // allow up to one adjacent rank difference (~200 Elo)

    // Session management for authentication
    private final ConcurrentHashMap<String, Integer> activeSessions = new ConcurrentHashMap<>(); // sessionToken -> userId
    private final ConcurrentHashMap<Integer, String> userToSession = new ConcurrentHashMap<>(); // userId -> sessionToken

    public synchronized sysMSG handleLogin(platformChannel channel, String username, String password) {
        UserStore store = UserStore.getInstance();
        List<User> matches = store.findByUserName(username);
        if (matches.isEmpty()) {
            return new sysMSG("LOGIN_FAIL", "Username not found");
        }
        User user = matches.getFirst();
        String hash = Helper.hashPassword(password, Constants.HASH_ALGORITHM);
        if (!hash.equals(user.getPasswordHash())) {
            return new sysMSG("LOGIN_FAIL", "Incorrect password");
        }
        if (!user.getIsActive()) {
            return new sysMSG("LOGIN_FAIL", "Account disabled");
        }
        // Check if already logged in
        if (userToSession.containsKey(user.getID())) {
            return new sysMSG("LOGIN_FAIL", "Already logged in from another location");
        }
        // Generate session token
        String token = UUID.randomUUID().toString();
        activeSessions.put(token, user.getID());
        userToSession.put(user.getID(), token);
        // Set on channel
        channel.setSessionToken(token);
        channel.setUserId(user.getID());
        return new sysMSG("LOGIN_SUCCESS", token, user.getlogin(), String.valueOf(user.getID()));
    }

    public synchronized void handleLogout(platformChannel channel) {
        if (channel.getSessionToken() != null) {
            Integer userId = activeSessions.remove(channel.getSessionToken());
            if (userId != null) {
                userToSession.remove(userId);
            }
        }
        channel.setSessionToken(null);
        channel.setUserId(0);
    }

    private static String normalizeGameType(String gameType) {
        if (gameType == null) {
            return "TTT";
        }
        String gt = gameType.trim().toUpperCase();
        if (gt.contains("C4") || gt.contains("CONNECT")) {
            return "C4";
        }
        return "TTT";
    }

    private static String gameIdForType(String gameType) {
        return "C4".equals(gameType) ? "c4" : "test";
    }

    private static String playerIdForC4(String playerId) {
        if ("P1".equals(playerId)) {
            return Player.PLAYER_1.toString();
        }
        if ("P2".equals(playerId)) {
            return Player.PLAYER_2.toString();
        }
        return playerId;
    }

    private static String[] parseNameEloPayload(String raw) {
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
        String normalized = code.trim().toUpperCase();
        String normalizedType = normalizeGameType(gameType);
        if (openLobbies.putIfAbsent(normalized, hostChannel) != null) {
            return false;
        }
        openLobbyGameTypes.put(normalized, normalizedType);
        openLobbyHostPayload.put(normalized, clientPayload != null ? clientPayload : "");
        hostChannel.ID = "host-" + normalized;
        hostChannel.setGameType(normalizedType);
        hostChannel.setGameID(gameIdForType(normalizedType));
        System.out.println("Lobby opened: " + normalized + " (" + normalizedType + ")");
        return true;
    }

    public synchronized sysMSG onJoinLobby(String code, platformChannel guestChannel, String guestPayload) {
        if (code == null || code.isBlank() || guestChannel == null) {
            return new sysMSG("LOBBY_ERR", "join", "No lobby with that code (check with host or try again)."
            );
        }
        String normalized = code.trim().toUpperCase();
        platformChannel host = openLobbies.get(normalized);
        if (host == null) {
            return new sysMSG("LOBBY_ERR", "join", "No lobby with that code (check with host or try again)."
            );
        }
        String gt = openLobbyGameTypes.get(normalized);
        String hostPayloadRaw = openLobbyHostPayload.get(normalized);
        String gameType = normalizeGameType(gt != null ? gt : host.getGameType());
        String gameId = gameIdForType(gameType);

        Rank hostRank = effectiveRankForChannel(host, gameType);
        Rank guestRank = effectiveRankForChannel(guestChannel, gameType);
        if (Math.abs(hostRank.getRankValue() - guestRank.getRankValue()) > MAX_LOBBY_RANK_GAP) {
            return new sysMSG(
                    "LOBBY_ERR",
                    "join",
                    "Opponent rank differs too much for this lobby. Try a different lobby or play a game at a closer rank.");
        }

        host.setGameType(gameType);
        host.setGameID(gameId);
        guestChannel.setGameType(gameType);
        guestChannel.setGameID(gameId);
        guestChannel.ID = "guest-" + normalized;
        p1 = host;
        p2 = guestChannel;
        if ("TTT".equals(gameType)) {
            TTTGame t = (TTTGame) games.get("test");
            Map<String, Player> turnIds = new HashMap<>();
            turnIds.put("P1", Player.PLAYER_1);
            turnIds.put("P2", Player.PLAYER_2);
            t.setPlayerIdToPlayer(turnIds);
        }
        String[] h = rosterEntryForChannel(host, hostPayloadRaw, gameType);
        String[] g = rosterEntryForChannel(guestChannel, guestPayload, gameType);
        String roster = h[0] + "|" + h[1] + "|" + g[0] + "|" + g[1];
        var rosterMsg = new sysMSG("LOBBY_ROSTER", normalized, "", roster);
        host.cliSend(rosterMsg);
        guestChannel.cliSend(rosterMsg);
        openLobbies.remove(normalized);
        openLobbyGameTypes.remove(normalized);
        openLobbyHostPayload.remove(normalized);
        tempGameReset();
        System.out.println("Lobby " + normalized + " matched; game ready (" + gameType + ").");
        return new sysMSG("LOBBY_OK", code.toUpperCase(), "JOIN");
    }

    private Rank effectiveRankForChannel(platformChannel channel, String gameType) {
        if (channel == null || channel.getUserId() <= 0) {
            return Rank.fromElo(1200);
        }
        User user = UserStore.getInstance().get(channel.getUserId());
        if (user == null) {
            return Rank.fromElo(1200);
        }
        ca.ucalgary.seng300.game.core.GameType gt = "C4".equals(gameType)
                ? ca.ucalgary.seng300.game.core.GameType.CONNECT4
                : ca.ucalgary.seng300.game.core.GameType.TIC_TAC_TOE;
        return Rank.fromElo(user.getEloForGame(gt));
    }

    private String[] rosterEntryForChannel(platformChannel channel, String payload, String gameType) {
        String name = "Player";
        int elo = 1200;
        if (channel != null && channel.getUserId() > 0) {
            User user = UserStore.getInstance().get(channel.getUserId());
            if (user != null) {
                name = user.getlogin();
                elo = user.getEloForGame("C4".equals(gameType) ? 
                        ca.ucalgary.seng300.game.core.GameType.CONNECT4 : ca.ucalgary.seng300.game.core.GameType.TIC_TAC_TOE);
            }
        }
        if (name == null || name.isBlank()) {
            String[] parsed = parseNameEloPayload(payload);
            name = parsed[0];
        }
        return new String[] { name, String.valueOf(elo) };
    }

    /**
     * Relays host "start game" to the guest using the same {@code GMJN} sysMSG pattern as the legacy client.
     */
    public synchronized void relayToGuestIfHost(platformChannel from, sysMSG sm) {
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
        p2.cliSend(new sysMSG("GMJN", fxml, b));
        System.out.println("GMJN relayed to guest → " + fxml);
    }

    /** Relays in-match chat between paired host/guest channels. */
    public synchronized void relayMatchChat(platformChannel from, sysMSG sm) {
        if (from == null || sm == null) {
            return;
        }
        platformChannel target = null;
        if (from == p1) {
            target = p2;
        } else if (from == p2) {
            target = p1;
        }
        if (target == null) {
            return;
        }
        String sender = sm.getFieldA() != null ? sm.getFieldA() : "Player";
        String body = sm.getFieldB() != null ? sm.getFieldB() : "";
        target.cliSend(new sysMSG("MATCH_CHAT", sender, body));
    }

    /** Relay a rematch request from one active player to the other. */
    public synchronized void relayRematchRequest(platformChannel from, sysMSG sm) {
        if (from == null || sm == null || p1 == null || p2 == null) {
            return;
        }
        platformChannel target = from == p1 ? p2 : (from == p2 ? p1 : null);
        if (target == null) {
            return;
        }
        String requester = sm.getFieldA() != null && !sm.getFieldA().isBlank() ? sm.getFieldA() : "Opponent";
        target.cliSend(new sysMSG("REMATCH_REQUEST", requester, ""));
    }

    /** Handle rematch consent; only reset when the requested player accepts. */
    public synchronized void onRematchResponse(platformChannel from, sysMSG sm) {
        if (from == null || sm == null || p1 == null || p2 == null) {
            return;
        }
        platformChannel target = from == p1 ? p2 : (from == p2 ? p1 : null);
        if (target == null) {
            return;
        }
        boolean accepted = "ACCEPT".equalsIgnoreCase(sm.getFieldA());
        String responder = sm.getFieldB() != null && !sm.getFieldB().isBlank() ? sm.getFieldB() : "Opponent";
        if (accepted) {
            p1.cliSend(new sysMSG("REMATCH_ACCEPTED", responder, ""));
            p2.cliSend(new sysMSG("REMATCH_ACCEPTED", responder, ""));
            tempGameReset();
            return;
        }
        target.cliSend(new sysMSG("REMATCH_DECLINED", responder, ""));
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
    /**
     * The forfeiting client loses; both players receive updated state.
     */
    public synchronized void onForfeit(platformChannel from) {
        if (p1 == null || p2 == null || from == null) {
            return;
        }
        String normalizedType = normalizeGameType(p1.getGameType());
        Player forfeiting = null;
        if (from == p1) {
            forfeiting = Player.PLAYER_1;
        } else if (from == p2) {
            forfeiting = Player.PLAYER_2;
        }
        if (forfeiting == null) {
            return;
        }
        if ("C4".equals(normalizedType)) {
            C4Game c4 = (C4Game) games.get("c4");
            c4.forfeit(forfeiting);
            System.out.println("Forfeit by " + forfeiting + " — broadcasting C4 state");
            p1.cliSend(GameStateNetworkCodec.encode(c4.getGameState()));
            p2.cliSend(GameStateNetworkCodec.encode(c4.getGameState()));
            return;
        }
        TTTGame t = (TTTGame) games.get("test");
        t.forfeit(forfeiting);
        System.out.println("Forfeit by " + forfeiting + " — broadcasting TTT state");
        p1.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
        p2.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
    }

    public void tempGameReset(){
        String activeType = p1 != null ? normalizeGameType(p1.getGameType()) : "TTT";
        if ("C4".equals(activeType)) {
            C4Game c4 = (C4Game) games.get("c4");
            c4.startGame(null, null);
            if (p1 != null) {
                p1.cliSend(GameStateNetworkCodec.encode(c4.getGameState()));
            }
            if (p2 != null) {
                p2.cliSend(GameStateNetworkCodec.encode(c4.getGameState()));
            }
            return;
        }
        TTTGame t = (TTTGame) games.get("test");
        t.tempReset();
        if (p1 != null) {
            p1.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
        }
        if (p2 != null) {
            p2.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
        }
    }

    public void gameEvent(platformChannel sender, String id, String type, PlayerMove p){
        if (p1 == null || p2 == null) {
            return;
        }
        String normalizedType = normalizeGameType(type);
        if ("TTT".equals(normalizedType)) {
            // Player channel stores the game type and ID for each client, no need to send it
            TTTGame t =(TTTGame) games.get(id);

            boolean valid = t.handleAction(new TTTPlayerAction(p.getPlayerID(),p.getActionType(), p.getRow(), p.getCol()) {
            });
            if (valid) {
                System.out.println(debugBoard((TTTGameState) t.getGameState()));
                p1.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
                p2.cliSend(GameStateNetworkCodec.encode(t.getGameState()));
            } else {
                // Send invalid move message to the sender
                sender.cliSend(new sysMSG("MOVE_INVALID", "Invalid move"));
            }
            return;
        }
        if ("C4".equals(normalizedType)) {
            C4Game c4 = (C4Game) games.get("c4");
            boolean valid = c4.handleAction(new C4PlayerAction(playerIdForC4(p.getPlayerID()), p.getActionType(), p.getRow(), p.getCol()));
            if (valid) {
                p1.cliSend(GameStateNetworkCodec.encode(c4.getGameState()));
                p2.cliSend(GameStateNetworkCodec.encode(c4.getGameState()));
            } else {
                // Send invalid move message to the sender
                sender.cliSend(new sysMSG("MOVE_INVALID", "Invalid move"));
            }
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
        games.put("c4", new C4Game());

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
