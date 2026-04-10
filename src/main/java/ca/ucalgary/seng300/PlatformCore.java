package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.platform.domain.FriendHistory;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.server.ServerLobby;
import ca.ucalgary.seng300.platform.server.platformChannel;
import ca.ucalgary.seng300.platform.services.MatchMakingService;
import ca.ucalgary.seng300.platform.store.*;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.domain.User;

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
    public FriendHistoryStore friendStore;
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

    private String getUsername(int userId) {
        List<User> users = userStore.findByUserID(userId);
        if (users != null && !users.isEmpty()) {
            return users.get(0).getlogin();
        }
        return "Player";
    }

    public String getPlayerScoreForLobby(String username, int userId, int gameId) {
        if (username == null || username.isEmpty()) {
            username = "Player";
        }
        if (playerStatsStore == null) {
            return "0";
        }
        List<PlayerStats> playersForGame = playerStatsStore.findPlayersByGameID(gameId);
        if (playersForGame == null) {
            return "0";
        }
        for (PlayerStats stats : playersForGame) {
            if (stats.getUserID() == userId) {
                return formatScore(stats.getScore());
            }
        }
        return "0";
    }

    public String getPlayerGamesPlayedForLobby(int userId, int gameId) {
        if (playerStatsStore == null) {
            return "0";
        }
        List<PlayerStats> playersForGame = playerStatsStore.findPlayersByGameID(gameId);
        if (playersForGame == null) {
            return "0";
        }
        for (PlayerStats stats : playersForGame) {
            if (stats.getUserID() == userId) {
                return String.valueOf(stats.getGameCounter());
            }
        }
        return "0";
    }

    public synchronized boolean onHostLobby(String code, int gameId, platformChannel hostChannel, String clientPayload) {
        if (code == null || code.isBlank() || hostChannel == null) {
            System.out.println("HOST_LOBBY rejected: code/hostChannel invalid");
            return false;
        }
        if (lobbies == null) {
            lobbies = new HashMap<>();
        }
        Integer hostUserId = hostChannel.getAuthenticatedUserId();
        if (hostUserId == null) {
            System.out.println("HOST_LOBBY rejected: host not authenticated");
            return false;
        }
        String hostUsername = hostChannel.getAuthenticatedSessionId() != null ? getUsername(hostUserId) : "Player";
        
        String lobbyid = code.trim().toUpperCase();
        System.out.println("HOST_LOBBY requested rawCode=" + code + " normalizedCode=" + lobbyid + " gameId=" + gameId);

        ServerLobby lob = new ServerLobby(lobbyid, gameId, hostChannel, hostUserId, hostUsername, this);
        if (lobbies.putIfAbsent(lobbyid, lob) != null) {
            System.out.println("HOST_LOBBY rejected: duplicate code. Existing lobbies=" + lobbies.keySet());
            return false;
        }
        if (chatservers != null && !chatservers.contains(lobbyid)) {
            chatservers.add(lobbyid);
        }
        System.out.println("Lobby opened: " + code + " (" + gameId + ") currentLobbies=" + lobbies.keySet());
        return true;
    }

    public synchronized boolean onJoinLobby(String code, int requestedGameId, platformChannel guestChannel, String guestPayload) {
        if (code == null || code.isBlank() || guestChannel == null || lobbies == null) {
            System.out.println("JOIN_LOBBY rejected early: rawCode=" + code + " gameId=" + requestedGameId
                    + " guestNull=" + (guestChannel == null) + " lobbiesNull=" + (lobbies == null));
            return false;
        }
        Integer guestUserId = guestChannel.getAuthenticatedUserId();
        if (guestUserId == null) {
            System.out.println("JOIN_LOBBY rejected: guest not authenticated");
            return false;
        }
        String guestUsername = getUsername(guestUserId);
        
        String lobbyid = code.trim().toUpperCase();
        System.out.println("JOIN_LOBBY lookup rawCode=" + code + " normalizedCode=" + lobbyid
                + " requestedGameId=" + requestedGameId + " availableLobbies=" + lobbies.keySet());
        ServerLobby sl = lobbies.get(lobbyid);
        if (sl == null) {
            System.out.println("JOIN_LOBBY rejected: no lobby found for " + lobbyid);
            return false;
        }
        if (requestedGameId > 0 && requestedGameId != sl.getGameId()) {
            System.out.println("JOIN_LOBBY rejected: game mismatch requested=" + requestedGameId
                    + " hostGameId=" + sl.getGameId());
            return false;
        }
        sl.joinGuest(guestChannel, guestUserId, guestUsername);
        sl.sendBoth(new sysMSG("PARTNER_JOINED", lobbyid, ""));
        sl.rosterSend();
        System.out.println("Lobby " + code + " paired (" + sl.getGameId() + ")");
        return true;
    }

    public synchronized sysMSG onCreateGame(String code, platformChannel requester) {
        if (code == null || code.isBlank() || requester == null || lobbies == null) {
            return new sysMSG("CREATE_GAME", "FAIL", "Starting the game failed.");
        }
        String lobbyid = code.trim().toUpperCase();
        ServerLobby sl = lobbies.get(lobbyid);
        if (sl == null || !sl.isHost(requester)) {
            return new sysMSG("CREATE_GAME", "FAIL", "Starting the game failed.");
        }
        platformChannel host = sl.getHost();
        platformChannel guest = sl.getGuest();
        if (host == null || guest == null) {
            return new sysMSG("CREATE_GAME", "FAIL", "Starting the game failed: lobby is not full.");
        }
        Integer hostUserId = host.getAuthenticatedUserId();
        Integer guestUserId = guest.getAuthenticatedUserId();
        if (hostUserId == null || hostUserId <= 0 || guestUserId == null || guestUserId <= 0) {
            return new sysMSG("CREATE_GAME", "FAIL", "Starting the game failed: both players must be signed in.");
        }

        PlayerStats hostStats = getOrCreatePlayerStats(sl.getGameId(), hostUserId);
        PlayerStats guestStats = getOrCreatePlayerStats(sl.getGameId(), guestUserId);
        if (!matchMakingService.canMatchPair(hostStats, guestStats)) {
            return new sysMSG("CREATE_GAME", "FAIL",
                    "Starting the game failed: players are outside the matchmaking range.");
        }
        sl.createGame();
        System.out.println("CREATE_GAME started for lobby " + lobbyid + " gameId=" + sl.getGameId());
        return new sysMSG("CREATE_GAME", "PASS", lobbyid);
    }

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

    public void sendMult (chatMessage m){
        for(platformChannel h : users){
            if (m.getServ() != null && m.getServ().equals(h.getLobby())) {
                h.cliSend(m);
            }
        }
    }

    public sysMSG authenticate(String user, String pass, platformChannel pl) {
        List<User> listOfUsers = userStore.findByUserName(user);
        if (listOfUsers.isEmpty()) {
            listOfUsers = userStore.findByUserEmail(user);
        }
        String incomingUsername = user;
        String incomingPassword = hashPassword(pass, HASH_ALGORITHM);

        if (listOfUsers.isEmpty()){
            return new sysMSG("AUTH", "FAIL", "Login failed, invalid email/username or password");
        }

        User matchedUser = listOfUsers.getFirst();
        boolean matchingIdentity = incomingUsername.equals(matchedUser.getlogin())
                || incomingUsername.equals(matchedUser.getEmail());
        List<SessionHistory> existingSessions = sessionHistoryStore.findSessionsByUserID(matchedUser.getID());
        if (!existingSessions.isEmpty()) {
            // Recover from stale sessions left behind by abrupt client termination.
            for (SessionHistory session : existingSessions) {
                sessionHistoryStore.delete(session);
            }
        }
        if (matchingIdentity && incomingPassword.equals(matchedUser.getPasswordHash())){
            int newSessionId = sessionHistoryStore.assignNewId();
            UUID UID = UUID.randomUUID();
            sessionHistoryStore.create(new SessionHistory(newSessionId, matchedUser.getID(), UID));
            if (pl != null) {
                pl.setAuthenticatedSession(matchedUser.getID(), UID);
            }
            String payload = matchedUser.getlogin() + "|" + matchedUser.getEmail();
            return new sysMSG("AUTH", "PASS", matchedUser.getID()+"", payload);
        }
        return new sysMSG("AUTH", "FAIL", "Login failed, invalid email/username or password");
    }

    public sysMSG createAccount(String user, String pass, String email){
        int newUserId = userStore.assignNewId();
        if(!userStore.findByUserName(user).isEmpty()){
            return new sysMSG("NEWU", "FAIL", "That username is already taken");
        }
        if(!isEmailValid(email, EMAIL_REGEX)){
            return new sysMSG("NEWU", "FAIL", "Please enter a valid email address");
        }
        userStore.create(new User(newUserId,user,email,hashPassword(pass,HASH_ALGORITHM)));
        return new sysMSG("NEWU", "PASS");
    }

    public sysMSG updateProfile(platformChannel channel, String newLogin, String newEmail, String newPasswordHash) {
        Integer userId = channel.getAuthenticatedUserId();
        if (userId == null) {
            return new sysMSG("UPDATE_PROFILE", "FAIL", "Not authenticated");
        }
        String sanitizedLogin = newLogin == null ? "" : newLogin.trim();
        String sanitizedEmail = newEmail == null ? "" : newEmail.trim();
        String sanitizedPasswordHash = newPasswordHash == null ? "" : newPasswordHash.trim();
        if (sanitizedLogin.isEmpty() || sanitizedEmail.isEmpty()) {
            return new sysMSG("UPDATE_PROFILE", "FAIL", "Display name and email are required");
        }

        User user = userStore.get(userId);
        if (user == null) {
            return new sysMSG("UPDATE_PROFILE", "FAIL", "User not found");
        }

        // Validate login (if changed)
        if (!user.getlogin().equals(sanitizedLogin)) {
            if (!userStore.findByUserName(sanitizedLogin).isEmpty()) {
                return new sysMSG("UPDATE_PROFILE", "FAIL", "Display name is already taken");
            }
        }

        // Validate email format
        if (!isEmailValid(sanitizedEmail, EMAIL_REGEX)) {
            return new sysMSG("UPDATE_PROFILE", "FAIL", "Invalid email address");
        }
        List<User> usersWithEmail = userStore.findByUserEmail(sanitizedEmail);
        if (!usersWithEmail.isEmpty() && usersWithEmail.getFirst().getID() != user.getID()) {
            return new sysMSG("UPDATE_PROFILE", "FAIL", "Email is already used by another account");
        }

        // Update user
        user.setlogin(sanitizedLogin);
        user.setEmail(sanitizedEmail);
        if (!sanitizedPasswordHash.isEmpty()) {
            user.setPasswordHash(sanitizedPasswordHash);
        }
        userStore.update(user);

        return new sysMSG("UPDATE_PROFILE", "PASS", "Profile updated successfully");
    }

    public sysMSG getAllFriendsByUserID(platformChannel channel) {
        Integer userId = channel.getAuthenticatedUserId();
        if (userId == null) {
            return new sysMSG("GET_FRIENDS", "FAIL", "Not authenticated");
        }

        List<FriendHistory> friends = friendStore.getAllFriendsByUserId(userId);
        List<String> friendNames = new ArrayList<>();
        for (FriendHistory fh : friends) {
            if (fh.isFriendShipActive()) {
                User f = userStore.get(fh.getFriendUserId());
                if (f != null) {
                    friendNames.add(f.getlogin());
                }
            }
        }

        String payload = String.join(",", friendNames);
        return new sysMSG("GET_FRIENDS", "PASS", payload);
    }

    public synchronized void onClientDisconnected(platformChannel channel) {
        if (channel == null) {
            return;
        }
        UUID sessionId = channel.getAuthenticatedSessionId();
        if (sessionId == null) {
            return;
        }
        List<SessionHistory> sessionsForUser = sessionHistoryStore.findSessionByUUID(sessionId);
        for (SessionHistory session : sessionsForUser) {
            sessionHistoryStore.delete(session);
        }
    }

    public synchronized void onClientLogout(platformChannel channel) {
        // Same as onClientDisconnected: delete the session UUID
        onClientDisconnected(channel);
    }

    public void handleMove(String id, platformChannel player, PlayerAction m){
        if (id == null || lobbies == null) {
            return;
        }
        ServerLobby sl = lobbies.get(id);
        if (sl == null) {
            return;
        }
        if (sl.getGameId() == ca.ucalgary.seng300.platform.constants.Game.TICK_TACK_TOE) {
            onHandleClick(id, player, m);
        } else if (sl.getGameId() == ca.ucalgary.seng300.platform.constants.Game.CONNECT_FOUR) {
            onDropPiece(id, player, m);
        }
    }

    public void onHandleClick(String id, platformChannel player, PlayerAction move) {
        handleValidatedMove(id, player, move);
    }

    public void onDropPiece(String id, platformChannel player, PlayerAction move) {
        handleValidatedMove(id, player, move);
    }

    private void handleValidatedMove(String id, platformChannel player, PlayerAction move) {
        if (id == null || player == null || lobbies == null) {
            return;
        }
        ServerLobby sl = lobbies.get(id);
        if (sl == null) {
            return;
        }
        boolean valid = sl.gameTurn(player, move);
        player.cliSend(new sysMSG("MOVE", valid ? "PASS" : "FAIL", valid ? "" : "Invalid move"));
        if (valid) {
            Object state = sl.currentGameState();
            if (state != null) {
                sl.sendBoth(state);
            }
            finalizeMatchIfNeeded(sl);
        }
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

    public synchronized void onChatNotification(platformChannel from, sysMSG sm) {
        if (from == null || lobbies == null || sm == null) {
            return;
        }
        String lobbyId = from.getLobby();
        if (lobbyId == null || lobbyId.isBlank()) {
            return;
        }
        ServerLobby sl = lobbies.get(lobbyId);
        if (sl == null || !sl.isParticipant(from)) {
            return;
        }
        platformChannel opponent = sl.getOpponent(from);
        if (opponent != null) {
            opponent.cliSend(new sysMSG("CHAT_NOTIFICATION", lobbyId, "", sm.getPayload()));
        }
    }

    public synchronized void onLeaveLobby(platformChannel from) {
        if (from == null || lobbies == null) {
            return;
        }
        String lobbyId = from.getLobby();
        if (lobbyId == null || lobbyId.isBlank()) {
            return;
        }
        ServerLobby sl = lobbies.get(lobbyId);
        if (sl == null || !sl.isParticipant(from)) {
            return;
        }
        
        platformChannel opponent = sl.getOpponent(from);
        if (sl.isHost(from)) {
            // Host leaving: notify guest and close lobby
            if (opponent != null) {
                opponent.cliSend(new sysMSG("CHAT_NOTIFICATION", lobbyId, "", "Host has left the lobby"));
                opponent.setLobby(null, 0);
            }
            from.setLobby(null, 0);
            lobbies.remove(lobbyId);
            System.out.println("Lobby " + lobbyId + " has been closed as the host left.");
        } else {
            // Guest leaving: update roster and keep lobby open
            sl.removeGuest();
            from.setLobby(null, 0);
            System.out.println("Guest left lobby " + lobbyId + ". Lobby remains open for host.");
        }
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
        
        sendUpdatedRosterAfterGame(sl);
    }

    private void sendUpdatedRosterAfterGame(ServerLobby sl) {
        if (sl == null) {
            return;
        }
        platformChannel host = sl.getHost();
        platformChannel guest = sl.getGuest();
        if (host == null || guest == null) {
            return;
        }
        Integer hostUserId = host.getAuthenticatedUserId();
        Integer guestUserId = guest.getAuthenticatedUserId();
        if (hostUserId == null || hostUserId <= 0 || guestUserId == null || guestUserId <= 0) {
            return;
        }
        
        String hostUsername = getUsername(hostUserId);
        String guestUsername = getUsername(guestUserId);
        String hostScore = getPlayerScoreForLobby(hostUsername, hostUserId, sl.getGameId());
        String guestScore = getPlayerScoreForLobby(guestUsername, guestUserId, sl.getGameId());
        String hostGamesPlayed = getPlayerGamesPlayedForLobby(hostUserId, sl.getGameId());
        String guestGamesPlayed = getPlayerGamesPlayedForLobby(guestUserId, sl.getGameId());
        
        String roster = hostUsername + "|" + hostScore + "|" + hostGamesPlayed + "|" + guestUsername + "|" + guestScore + "|" + guestGamesPlayed;
        sysMSG rosterMsg = new sysMSG("LOBBY_ROSTER", sl.getLobbyCode(), "", roster);
        host.cliSend(rosterMsg);
        guest.cliSend(rosterMsg);
    }

    public void onRosterRequest(String lobbyCode, platformChannel requester) {
        System.out.println("PlatformCore.onRosterRequest: code=" + lobbyCode);
        ServerLobby sl = lobbies.get(lobbyCode);
        if (sl != null) {
            System.out.println("PlatformCore.onRosterRequest: found lobby, calling rosterSend");
            sl.rosterSend(requester);
        } else {
            System.out.println("PlatformCore.onRosterRequest: lobby not found for code: " + lobbyCode);
        }
    }

    public void onPlayerStatsRequest(String userIdStr, String gameIdStr, platformChannel requester) {
        if (userIdStr == null || gameIdStr == null || requester == null) {
            return;
        }
        try {
            int userId = Integer.parseInt(userIdStr);
            int gameId = Integer.parseInt(gameIdStr);
            
            PlayerStats stats = getOrCreatePlayerStats(gameId, userId);
            sendPlayerStatsUpdate(requester, stats);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void onAiGameResult(platformChannel channel, String gameIdStr, String result) {
        Integer userId = channel.getAuthenticatedUserId();
        if (userId == null || gameIdStr == null || result == null) {
            return;
        }
        try {
            int gameId = Integer.parseInt(gameIdStr);
            PlayerStats stats = getOrCreatePlayerStats(gameId, userId);
            
            if ("WIN".equalsIgnoreCase(result)) {
                stats.updateStatsWin(1.0);
            } else if ("LOSS".equalsIgnoreCase(result)) {
                stats.updateStatsLose(1.0);
            } else {
                stats.updateStatsWin(0.0); // Draw/Forfeit
            }
            playerStatsStore.update(stats);
            sendPlayerStatsUpdate(channel, stats);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public sysMSG onLeaderboardRequest(String gameIdStr) {
        if (gameIdStr == null) {
            return new sysMSG("LEADERBOARD_DATA", "FAIL", "Invalid gameId");
        }
        try {
            int gameId = Integer.parseInt(gameIdStr);
            List<PlayerStats> leaderboard = ca.ucalgary.seng300.platform.services.LeaderBoardService.getInstance()
                    .getLeaderBoard(true, gameId, 10); // top 10

            StringBuilder sb = new StringBuilder();
            for (PlayerStats stats : leaderboard) {
                User user = userStore.get(stats.getUserID());
                String username = user != null ? user.getlogin() : "Player";
                String rank = ca.ucalgary.seng300.platform.utils.Helper.getRank(stats.getGameCounter(), stats.getScore()).getDisplayName();
                
                sb.append(username).append(":")
                  .append(rank).append(":")
                  .append(formatScore(stats.getScore())).append(":")
                  .append(stats.getGameCounter()).append(";");
            }
            return new sysMSG("LEADERBOARD_DATA", gameIdStr, "SUCCESS", sb.toString());
        } catch (NumberFormatException e) {
            return new sysMSG("LEADERBOARD_DATA", "FAIL", "Invalid gameId format");
        }
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

    // Number of active users
    private int cActive = 0;

    public PlatformCore() throws IOException, InterruptedException {
        this(true);
    }

    public PlatformCore(boolean startServer) throws IOException, InterruptedException {
        /** database initialisation, replace null with the file path **/
        userStore = UserStore.getInstance();
        friendStore = FriendHistoryStore.getInstance();
        sessionHistoryStore = SessionHistoryStore.getInstance();
        gameHistoryStore = GameHistoryStore.getInstance();
        playerStatsStore = PlayerStatsStore.getInstance();
        participantStore = ParticipantStore.getInstance();

        // list of threads
        users = new ArrayList<platformChannel>();
        connected = new ArrayList<Integer>();
        chatservers = new ArrayList<String>();
        lobbies = new HashMap<>();
        if (startServer) {
            startServerLoop();
        }
    }

    private void startServerLoop() throws IOException, InterruptedException {
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
        if (args != null && args.length > 0 && "NO_SERVER".equals(args[0])) {
            return;
        }
        try {
            new PlatformCore(); // Start the server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
