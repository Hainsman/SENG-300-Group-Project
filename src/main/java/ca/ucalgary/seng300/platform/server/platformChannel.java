package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class platformChannel extends Thread {
    private Socket socket;
    private String lobby;

    private PlatformCore serv;
    private ObjectOutputStream outServ;
    private ObjectInputStream inCli;
    public  String ID;
    private Integer authenticatedUserId;
    private UUID authenticatedSessionId;


    public platformChannel(Socket socket, PlatformCore serv) {
        this.socket = socket;
        this.serv = serv;
    }

    public void cliSend (Object o){
        try{
            outServ.writeObject(o);
            outServ.flush();
            outServ.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setLobby(String lob, int gameNumber){
        lobby = lob;
    }

    public String getLobby() {
        return lobby;
    }

    public void setAuthenticatedSession(int userId, UUID sessionId) {
        this.authenticatedUserId = userId;
        this.authenticatedSessionId = sessionId;
    }

    public Integer getAuthenticatedUserId() {
        return authenticatedUserId;
    }

    public UUID getAuthenticatedSessionId() {
        return authenticatedSessionId;
    }

    private void handleSys(sysMSG sm) {
        switch (sm.getType()) {
            case "AUTH":
                cliSend(serv.authenticate(sm.getFieldA(), sm.getFieldB(), this));
                break;
            case "NEWU":
                cliSend(serv.createAccount(sm.getFieldA(), sm.getFieldB(), sm.getPayload()));
                break;
            case "HOST_LOBBY":
                String code = sm.getFieldA();
                int gt;
                try {
                    gt = sm.getFieldB() != null ? Integer.parseInt(sm.getFieldB()) : Game.TICK_TACK_TOE;
                } catch (NumberFormatException e) {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST", "Hosting a lobby failed: invalid game type."));
                    break;
                }
                String lobbyPayload = sm.getPayload() != null ? sm.getPayload() : "";
                boolean ok = serv.onHostLobby(code, gt, this, lobbyPayload);
                if (ok) {
                    cliSend(new sysMSG("LOBBY_OK", code.toUpperCase(), "HOST"));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST", "Hosting a lobby failed."));
                }
                break;
            case "JOIN_LOBBY":
                int requestedGameId = 0;
                try {
                    if (sm.getFieldB() != null && !sm.getFieldB().isBlank()) {
                        requestedGameId = Integer.parseInt(sm.getFieldB());
                    }
                } catch (NumberFormatException e) {
                    requestedGameId = 0;
                }
                System.out.println("JOIN_LOBBY received for code " + sm.getFieldA() + " gameId=" + requestedGameId);
                if (serv.onJoinLobby(sm.getFieldA(), requestedGameId, this, sm.getPayload())) {
                    cliSend(new sysMSG("LOBBY_OK", sm.getFieldA(), "JOIN"));
                }
                break;
            case "CREATE_GAME":
                sysMSG createGameResponse = serv.onCreateGame(sm.getFieldA(), this);
                if (!"PASS".equals(createGameResponse.getFieldA())) {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST",
                            createGameResponse.getFieldB() != null ? createGameResponse.getFieldB() : "Starting the game failed."));
                }
                break;
            case "FORFEIT":
                serv.onForfeit(this);
                break;
            case "reset":
                serv.onRematch(this);
                break;
            case "REMATCH_RESPONSE":
                serv.onRematchResponse(this, "ACCEPT".equalsIgnoreCase(sm.getFieldA()));
                break;
            case "ROSTER_REQUEST":
                System.out.println("platformChannel.handleSys: ROSTER_REQUEST received for code: " + sm.getFieldA());
                serv.onRosterRequest(sm.getFieldA(), this);
                break;
            case "PLAYER_STATS_REQUEST":
                // fieldA: userId (string), fieldB: gameId (string)
                serv.onPlayerStatsRequest(sm.getFieldA(), sm.getFieldB(), this);
                break;
            case "LOGOUT":
                serv.onClientLogout(this);
                break;
            case "UPDATE_PROFILE":
                // fieldA: new login, fieldB: new email
                // payload: optional password hash
                cliSend(serv.updateProfile(this, sm.getFieldA(), sm.getFieldB(), sm.getPayload()));
                break;
            case "AI_GAME_RESULT":
                // fieldA: gameId, fieldB: result (WIN, LOSS, DRAW)
                serv.onAiGameResult(this, sm.getFieldA(), sm.getFieldB());
                break;
            case "LEADERBOARD_REQUEST":
                // fieldA: gameId
                cliSend(serv.onLeaderboardRequest(sm.getFieldA()));
                break;
            case "GET_FRIENDS":
                cliSend(serv.getAllFriendsByUserID(this));
                break;
            case "GAME_HISTORY_REQUEST":
                cliSend(serv.onGameHistoryRequest(this));
                break;
            case "CHAT_NOTIFICATION":
                serv.onChatNotification(this, sm);
                break;
            case "LEAVE_LOBBY":
                serv.onLeaveLobby(this);
                break;

            default:
                break;
        }
    }

    @Override
    public void run() {
        try{
            outServ = new ObjectOutputStream(socket.getOutputStream());
            inCli = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        while (!interrupted()) {
            Object message;
            try {
                message = inCli.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (message instanceof String str && "exit".equals(str)) {
                break;
            }

            switch (message) {
                case PlayerAction p -> {
                    System.out.println("player action arrived");
                    serv.handleMove(lobby,this, p);
                }
                case sysMSG s -> handleSys(s);
                case chatMessage cm -> {
                    if (serv.chatservers != null && serv.chatservers.contains(cm.getServ())) {
                        serv.sendMult(cm);
                    }
                }
                default -> System.out.println("def " + message);
            }
        }

        try{
            socket.close();
        }catch (Exception e){
            System.out.println("socket already closed");
        }

        serv.onClientDisconnected(this);

        System.out.println("Client " + ID + " " + socket.getInetAddress() + " has disconnected.");
    }
}
