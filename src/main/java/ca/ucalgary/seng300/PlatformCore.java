package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.server.ServerLobby;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.services.MatchMakingService;
import ca.ucalgary.seng300.platform.store.*;

import java.net.*;
import java.io.*;
import java.util.*;

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
    private final MatchMakingService matchMakingService = new MatchMakingService();

   public synchronized boolean onHostLobby(String code, String gameType, platformChannel hostChannel, String clientPayload) {
        if (code == null || code.isBlank() || hostChannel == null) {
            System.out.println("HOST_LOBBY rejected: code/hostChannel invalid");
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
            System.out.println("JOIN_LOBBY rejected early: rawCode=" + code + " gameId=" + code
                    + " guestNull=" + (guestChannel == null) + " lobbiesNull=" + (lobbies == null));
            return false;
        }
        String lobbyid = code.trim().toUpperCase();
        System.out.println("JOIN_LOBBY lookup rawCode=" + code + " normalizedCode=" + lobbyid
                + " requestedGameId=" + code + " availableLobbies=" + lobbies.keySet());
        ServerLobby sl = lobbies.get(lobbyid);
        if (sl == null) {
            return false;
        }
        sl.joinGuest(guestChannel, guestPayload);
        sl.rosterSend();
        System.out.println("Lobby " + code + " matched; game ready");
        return true;
    }

    public void startLobby(String code){
        ServerLobby sl = lobbies.get(code);
        sl.gameStart();
    }

    /** Relays in-match chat between paired host/guest channels. */
    public synchronized void relayMatchChat(String lobbyid, String sender, String Msg) {
        ServerLobby sl = lobbies.get(lobbyid);
        sl.sendBoth(new sysMSG("MATCH_CHAT", sender, Msg));
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

    public sysMSG authenticate(String user, String pass, platformChannel pl) {
        List<User> listOfUsers = userStore.findByUserName(user);
        int newSessionId = sessionHistoryStore.assignNewId();
        System.out.println("loginprocessed");
        UUID UID = UUID.randomUUID();
        String incomingUsername = user;
        String incomingPassword = hashPassword(pass, HASH_ALGORITHM);

        if (listOfUsers.isEmpty()){
            System.out.println("loginreply sent");
            return new sysMSG("AUTH", "FAIL", "Error Username not found");
        }

        if (incomingUsername.equals(listOfUsers.getFirst().getlogin()) && incomingPassword.equals(listOfUsers.getFirst().getPasswordHash())){
            sessionHistoryStore.create(new SessionHistory(newSessionId, listOfUsers.getFirst().getID(), UID));
            System.out.println("loginreply sent");
            pl.setUserID(listOfUsers.getFirst().getID());
            return new sysMSG("AUTH", "PASS", listOfUsers.getFirst().getID()+"",listOfUsers.getFirst().getlogin());
        }System.out.println("loginreply sent");
        return new sysMSG("AUTH", "FAIL", "Login failed, invalid username or password");
    }

    public sysMSG createAccount(String user, String pass, String email){
        List<User> check = userStore.findByUserName(user);
        int newSessionId = sessionHistoryStore.assignNewId();
        UUID UID = UUID.randomUUID();
        int newUserId = userStore.assignNewId();
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
        sl.gameTurn(m);
    }
    /**
     * Tic-Tac-Toe only: the forfeiting client loses and both receive the updated game state.
     */
    public void onForfeit(String lobbyid) {
        ServerLobby sl = lobbies.get(lobbyid);
        applyMatchOutcome(sl, null, true);
        sl.sendBoth(new sysMSG("FORFEIT", "ACCEPT"));

    }

    public synchronized void onRematch(platformChannel requester, String type) {
        ServerLobby sl = lobbies.get(requester.lobby);
        switch (type){
            case "REQUEST":
                //target.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("REMATCH_REQUEST", requester, ""));
                sl.setPendingRematchRequester(requester);
                sl.sendOther(requester, new sysMSG("REMATCH", "REQUEST"));
                break;
            case "ACCEPT":
                //p2.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("REMATCH_ACCEPTED", responder, ""));
                applyMatchOutcome(sl, null, true);
                sl.gameReset();
                sl.sendBoth(new sysMSG("REMATCH", "ACCEPT"));
                break;
            default:
                sl.setPendingRematchRequester(null);
                sl.sendOther(requester, new sysMSG("REMATCH", "DECLINED"));
                //target.cliSend(new ca.ucalgary.seng300.platform.messages.sysMSG("REMATCH_DECLINED", responder, ""));
                break;

        }
    }
/*
    private void finalizeMatchIfNeeded(ServerLobby sl) {
        if (sl == null || sl.hasRecordedResult() || !sl.isGameOver()) {
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
        applyMatchOutcome(sl, winner, false);
    }*/

    private void applyMatchOutcome(ServerLobby sl, Player winner, boolean forfeit) {
        if (sl == null || sl.hasRecordedResult()) {
            return;
        }
        platformChannel host = sl.getHost();
        platformChannel guest = sl.getGuest();
        if (host == null || guest == null) {
            return;
        }
        Integer hostUserId = host.getAuthenticatedUserId();
        Integer guestUserId = guest.getAuthenticatedUserId();
        if (hostUserId == null || guestUserId == null) {
            return;
        }

        PlayerStats hostStats = null;//getOrCreatePlayerStats(sl.getGameType(), hostUserId);
        PlayerStats guestStats = null;//getOrCreatePlayerStats(sl.getGameType(), guestUserId);
        double delta = forfeit ? 0.0 : 1.0;

        if (winner == Player.PLAYER_1) {
            hostStats.updateStatsWin(delta);
            guestStats.updateStatsLose(delta);
        } else if (winner == Player.PLAYER_2) {
            hostStats.updateStatsLose(delta);
            guestStats.updateStatsWin(delta);
        } else {
            hostStats.updateStatsWin(0.0);
            guestStats.updateStatsLose(0.0);
        }

        playerStatsStore.update(hostStats);
        playerStatsStore.update(guestStats);
        sl.markResultRecorded();

        sendPlayerStatsUpdate(host, hostStats);
        sendPlayerStatsUpdate(guest, guestStats);
    }

    private void sendPlayerStatsUpdate(platformChannel channel, PlayerStats stats) {
        if (channel == null || stats == null) {
            return;
        }
        String rank = ca.ucalgary.seng300.platform.utils.Helper
                .getRank(stats.getGameCounter(), stats.getScore())
                .getDisplayName();
        channel.cliSend(new sysMSG(
                "PLAYER_STATS",
                formatScore(stats.getScore()),
                String.valueOf(stats.getGameCounter()),
                rank
        ));
    }

    private String formatScore(double score) {
        if (score == Math.rint(score)) {
            return String.valueOf((int) score);
        }
        return String.format("%.1f", score);
    }
    // MESS ENDS
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
        chatservers = new ArrayList<String>();
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
