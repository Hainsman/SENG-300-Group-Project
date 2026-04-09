package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.server.GameSessionRecord;
import ca.ucalgary.seng300.platform.server.ServerLobby;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.services.MatchMakingService;
import ca.ucalgary.seng300.platform.store.*;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ca.ucalgary.seng300.platform.constants.Constants.EMAIL_REGEX;
import static ca.ucalgary.seng300.platform.constants.Constants.HASH_ALGORITHM;
import static ca.ucalgary.seng300.platform.utils.Helper.hashPassword;
import static ca.ucalgary.seng300.platform.utils.Helper.isEmailValid;

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
/*
        PlayerStats hostStats = getOrCreatePlayerStats(sl.getGameId(), hostUserId);
        PlayerStats guestStats = getOrCreatePlayerStats(sl.getGameId(), guestUserId);
        MatchMakingService.removePlayer(hostStats); //remove duplicate
        MatchMakingService.removePlayer(guestStats); //remove duplicate
        matchMakingService.addPlayerToQueue(hostStats);
        matchMakingService.addPlayerToQueue(guestStats);
        List<PlayerStats> matchedPlayers = matchMakingService.matchPlayers(sl.getGameId());
        if (!containsMatchedPair(matchedPlayers, hostUserId, guestUserId)) {
            MatchMakingService.removePlayer(hostStats);
            MatchMakingService.removePlayer(guestStats);
            return new sysMSG("CREATE_GAME", "FAIL",
                    "Starting the game failed: player scores must be within 10 points.");
        }


 */
private PlayerStats getOrCreatePlayerStats(int gameId, int userId) {
    List<PlayerStats> playersForGame = playerStatsStore.findPlayersByGameID(gameId);
    for (PlayerStats stats : playersForGame) {
        if (stats.getUserID() == userId) {
            return stats;
        }
    }
    PlayerStats created = new PlayerStats(playerStatsStore.assignNewId(), gameId, userId);
    playerStatsStore.create(created);
    return created;
}

    private boolean containsMatchedPair(List<PlayerStats> matchedPlayers, int hostUserId, int guestUserId) {
        if (matchedPlayers == null || matchedPlayers.size() != 2) {
            return false;
        }
        boolean foundHost = false;
        boolean foundGuest = false;
        for (PlayerStats player : matchedPlayers) {
            if (player.getUserID() == hostUserId) {
                foundHost = true;
            } else if (player.getUserID() == guestUserId) {
                foundGuest = true;
            }
        }
        return foundHost && foundGuest;
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
        sl.gameTurn(player, m);
    }
    /**
     * Tic-Tac-Toe only: the forfeiting client loses and both receive the updated game state.
     */
    public synchronized void onForfeit(platformChannel from) {
        if (from == null || lobbies == null) {
            return;
        }
        String lobbyId = from.getLobby();
        if (lobbyId == null || lobbyId.isBlank()) {
            return;
        }
        ServerLobby sl = lobbies.get(lobbyId);
        if (sl == null || !sl.isParticipant(from) || sl.hasRecordedResult()) {
            return;
        }
        platformChannel opponent = sl.getOpponent(from);
        if (opponent != null) {
            opponent.cliSend(new sysMSG("FORFEIT_NOTICE", lobbyId, "", "The other player has forfeited."));
        }
        applyMatchOutcome(sl, null, true);
        sl.sendBoth(new sysMSG("FORFEIT_COMPLETE", lobbyId, ""));
    }

    public synchronized void onRematch(platformChannel requester) {
        if (requester == null || lobbies == null) {
            return;
        }
        String lobbyId = requester.getLobby();
        if (lobbyId == null || lobbyId.isBlank()) {
            return;
        }
        ServerLobby sl = lobbies.get(lobbyId);
        if (sl == null || !sl.isParticipant(requester)) {
            return;
        }
        if (sl.getHost() == null || sl.getGuest() == null || !sl.isGameOver()) {
            return;
        }
        platformChannel opponent = sl.getOpponent(requester);
        if (opponent == null) {
            return;
        }
        if (sl.getPendingRematchRequester() != null) {
            return;
        }
        sl.setPendingRematchRequester(requester);
        opponent.cliSend(new sysMSG("REMATCH_REQUEST", lobbyId, ""));
    }

    public synchronized void onRematchResponse(platformChannel responder, boolean accepted) {
        if (responder == null || lobbies == null) {
            return;
        }
        String lobbyId = responder.getLobby();
        if (lobbyId == null || lobbyId.isBlank()) {
            return;
        }
        ServerLobby sl = lobbies.get(lobbyId);
        if (sl == null || !sl.isParticipant(responder)) {
            return;
        }
        platformChannel requester = sl.getPendingRematchRequester();
        if (requester == null || requester == responder) {
            return;
        }
        platformChannel opponent = sl.getOpponent(responder);
        if (opponent != requester) {
            return;
        }

        if (accepted) {
            sl.resetGame();
            return;
        }

        sl.setPendingRematchRequester(null);
        sl.sendBoth(new sysMSG("REMATCH_DECLINED", lobbyId, ""));
    }

    private void finalizeMatchIfNeeded(ServerLobby sl) {
        if (sl == null || sl.hasRecordedResult() || !sl.isGameOver()) {
            return;
        }
        GameState state = sl.currentTypedGameState();
        if (state == null) {
            return;
        }
        Player winner = null;
        if (state.getState() == ca.ucalgary.seng300.game.core.State.PLAYER_1_WIN) {
            winner = Player.PLAYER_1;
        } else if (state.getState() == ca.ucalgary.seng300.game.core.State.PLAYER_2_WIN) {
            winner = Player.PLAYER_2;
        }
        applyMatchOutcome(sl, winner, false);
    }

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

        PlayerStats hostStats = getOrCreatePlayerStats(sl.getGameId(), hostUserId);
        PlayerStats guestStats = getOrCreatePlayerStats(sl.getGameId(), guestUserId);
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
