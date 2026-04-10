package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTRuleValidator;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.LobbyBridge;
import javafx.application.Application;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientCore extends Thread{
    public String CID;
    public String username;
    public String email;
    private ObjectInputStream servRead;
    private ObjectOutputStream servSend;
    private CentralController ioMain;
    public volatile char flg = 'n';
    public String gameType;
    private volatile String myPlayerId;
    public volatile  boolean upFlag = false;
    public volatile GameState lastUpdate;
    private volatile Consumer<sysMSG> authResponseHandler;
    private volatile Consumer<sysMSG> newUserResponseHandler;
    private volatile Consumer<sysMSG> moveResponseHandler;
    private volatile Consumer<sysMSG> playerStatsHandler;
    private volatile Consumer<sysMSG> rematchHandler;
    private volatile Consumer<chatMessage> chatMessageHandler;
    
    public boolean isConnected() {
        return servSend != null && servRead != null;
    }

    /** Mapped lobby slot for moves: {@code P1} = host (X), {@code P2} = guest (O). Set from {@code LOBBY_OK}. */
    public Player getMyNetworkRole() {
        if ("P1".equals(myPlayerId)) {
            return Player.PLAYER_1;
        }
        if ("P2".equals(myPlayerId)) {
            return Player.PLAYER_2;
        }
        return null;
    }

    public boolean submitMove(int row, int col){
        if (!isConnected()) {
            return false;
        }
        String pid = (myPlayerId != null && !myPlayerId.isBlank())
                ? myPlayerId
                : ((CID != null && !CID.isBlank()) ? CID : "player");
        PlayerAction action;
        if (lastUpdate instanceof C4GameState) {
            Player role = getMyNetworkRole();
            String c4PlayerId = role != null ? role.toString() : pid;
            action = new C4PlayerAction(c4PlayerId, "MOVE", row, col);
        } else {
            action = new TTTPlayerAction(pid, "MOVE", row, col);
        }
        try{
            servSend.writeObject(action);
            servSend.flush();
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void msgSend(Object o) {
        if (!isConnected()) {
            return;
        }
        try{
            servSend.writeObject(o);
            servSend.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientCore(CentralController c){ioMain = c;}

    public GameState getLastUpdate() {
        return lastUpdate;
    }

    public void setAuthResponseHandler(Consumer<sysMSG> handler) {
        authResponseHandler = handler;
    }

    public void setNewUserResponseHandler(Consumer<sysMSG> handler) {
        newUserResponseHandler = handler;
    }

    public void setMoveResponseHandler(Consumer<sysMSG> handler) {
        moveResponseHandler = handler;
    }

    public void setPlayerStatsHandler(Consumer<sysMSG> handler) {
        playerStatsHandler = handler;
    }

    private volatile Consumer<sysMSG> profileUpdateResponseHandler;
    private volatile Consumer<sysMSG> friendsListHandler;
    private volatile Consumer<sysMSG> leaderboardHandler;

    public void setProfileUpdateResponseHandler(Consumer<sysMSG> handler) {
        profileUpdateResponseHandler = handler;
    }

    public void setFriendsListHandler(Consumer<sysMSG> handler) {
        friendsListHandler = handler;
    }

    public void setLeaderboardHandler(Consumer<sysMSG> handler) {
        leaderboardHandler = handler;
    }

    public void setRematchHandler(Consumer<sysMSG> handler) {
        rematchHandler = handler;
    }

    public void setChatMessageHandler(Consumer<chatMessage> handler) {
        chatMessageHandler = handler;
    }

    public void sendLogout() {
        if (!isConnected()) {
            return;
        }
        try {
            servSend.writeObject(new sysMSG("LOGOUT", "", ""));
            servSend.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void run() {
        try {
            Platform.setImplicitExit(false);
            // Connect to the server at localhost on port 2020
            Socket socket = new Socket();
            try {
                // try to establish a connection with a 1 second timeout
                socket.setSoTimeout(1000);
                socket.connect(new InetSocketAddress("localhost", 2020));
                socket.setSoTimeout(0);
            }
            catch (Exception e) {
                System.out.println("Client unable to connect to game server (is PlatformCore running on port 2020?): " + e.getMessage());
                try {
                    socket.close();
                } catch (IOException ignored) { }
                return;
            }

            // Set up input and output streams for talking back and forth
            servSend = new ObjectOutputStream(socket.getOutputStream());
            servRead = new ObjectInputStream(socket.getInputStream());
            //System.out.println("objreads ready");
            Object message;


            // wait for first client response
            // if the client is closed unexpectedly readline returns null so this catches any error
            while (!interrupted()) {
                // wait for an incoming message from the server
                try{
                    message = servRead.readObject();
                } catch (IOException e) {
                    continue;
                }

                if (message instanceof String s && "exit".equals(s)) {
                    break;
                }

                switch(message){
                    case sysMSG sm:
                        switch (sm.getType()){
                            case "LOGS":
                                username = sm.getFieldA();
                                CID = sm.getFieldB();
                                flg = 's';
                                upFlag = true;
                                break;
                            case "LOGF":
                                flg = 'f';
                                upFlag = true;
                                break;
                            case "AUTH":
                                if ("PASS".equals(sm.getFieldA())) {
                                    CID = sm.getFieldB();
                                    username = sm.getPayload();
                                    // payload can now contain "username|email"
                                    if (username != null && username.contains("|")) {
                                        String[] parts = username.split("\\|", 2);
                                        username = parts[0];
                                        email = parts[1];
                                    }
                                    flg = 's';
                                } else {
                                    flg = 'f';
                                }
                                upFlag = true;
                                Consumer<sysMSG> handler = authResponseHandler;
                                if (handler != null) {
                                    Platform.runLater(() -> handler.accept(sm));
                                }
                                break;
                            case "NEWU":
                                Consumer<sysMSG> newUserHandler = newUserResponseHandler;
                                if (newUserHandler != null) {
                                    Platform.runLater(() -> newUserHandler.accept(sm));
                                }
                                break;
                            case "GMJN":
                                upFlag = true;
                                {
                                    String screen = sm.getFieldA();
                                    if (screen != null && !screen.isBlank()) {
                                        final String fxml = screen;
                                        Platform.runLater(() -> {
                                            LobbyBridge.clearLobbyUiCallbacks();
                                            CentralController.moveTo(fxml);
                                        });
                                    }
                                }
                                break;
                            case "LOBBY_OK":
                                if ("HOST".equals(sm.getFieldB())) {
                                    myPlayerId = "P1";
                                } else if ("JOIN".equals(sm.getFieldB())) {
                                    myPlayerId = "P2";
                                }
                                LobbyBridge.notifyLobbyOk(sm.getFieldB(), sm.getFieldA());
                                break;
                            case "LOBBY_ERR":
                                LobbyBridge.notifyLobbyFail(sm.getFieldB() != null ? sm.getFieldB() : sm.getFieldA());
                                break;
                            case "PARTNER_JOINED":
                                LobbyBridge.notifyPartnerJoined();
                                break;
                            case "LOBBY_ROSTER":
                                LobbyBridge.notifyLobbyRoster(sm.getPayload());
                                break;
                            case "LOBBY_ROSTER_UPDATED":
                                LobbyBridge.notifyLobbyRoster(sm.getPayload());
                                break;
                            case "MOVE":
                                Consumer<sysMSG> moveHandler = moveResponseHandler;
                                if (moveHandler != null) {
                                    Platform.runLater(() -> moveHandler.accept(sm));
                                }
                                break;
                            case "PLAYER_STATS":
                                Consumer<sysMSG> statsHandler = playerStatsHandler;
                                if (statsHandler != null) {
                                    Platform.runLater(() -> statsHandler.accept(sm));
                                }
                                String score = sm.getFieldA() != null ? sm.getFieldA() : "0";
                                LobbyBridge.notifyPlayerStatsUpdated(score);
                                break;
                            case "UPDATE_PROFILE":
                                Consumer<sysMSG> updateHandler = profileUpdateResponseHandler;
                                if (updateHandler != null) {
                                    Platform.runLater(() -> updateHandler.accept(sm));
                                }
                                break;
                            case "GET_FRIENDS":
                                Consumer<sysMSG> fHandler = friendsListHandler;
                                if (fHandler != null) {
                                    Platform.runLater(() -> fHandler.accept(sm));
                                }
                                break;
                            case "LEADERBOARD_DATA":
                                Consumer<sysMSG> lbHandler = leaderboardHandler;
                                if (lbHandler != null) {
                                    Platform.runLater(() -> lbHandler.accept(sm));
                                }
                                break;
                            case "REMATCH_REQUEST":
                            case "REMATCH_DECLINED":
                            case "FORFEIT_NOTICE":
                            case "FORFEIT_COMPLETE":
                                Consumer<sysMSG> currentRematchHandler = rematchHandler;
                                if (currentRematchHandler != null) {
                                    Platform.runLater(() -> currentRematchHandler.accept(sm));
                                }
                                break;
                            default:
                                break;
                        }
                        break;


                    case GameState g:
                        System.out.println("[DEBUG_LOG] a gamestate arrived: " + g.getClass().getSimpleName());
                        lastUpdate = g;
                        if (g instanceof TTTGameState tttState) {
                            System.out.println("[DEBUG_LOG] Board: " + debugBoard(tttState));
                        }
                        Platform.runLater(() -> {
                            if (g instanceof TTTGameState tttState && ioMain != null) {
                                if (ioMain.ttt != null) {
                                    System.out.println("[DEBUG_LOG] Updating TTT UI");
                                    ioMain.ttt.refreshFromGameStateUpdate(tttState);
                                } else {
                                    System.out.println("[DEBUG_LOG] TTT UI reference is NULL in ioMain");
                                }
                            } else if (g instanceof C4GameState c4State && ioMain != null) {
                                if (ioMain.connect4 != null) {
                                    System.out.println("[DEBUG_LOG] Updating C4 UI");
                                    ioMain.connect4.refreshFromGameStateUpdate(c4State);
                                } else {
                                    System.out.println("[DEBUG_LOG] C4 UI reference is NULL in ioMain");
                                }
                            }
                            LobbyBridge.notifyGameState();
                        });

                        break;
                    case chatMessage c:
                        Consumer<chatMessage> chatHandler = chatMessageHandler;
                        if (chatHandler != null) {
                            Platform.runLater(() -> chatHandler.accept(c));
                        }
                        break;
                    //case matchMaking m:

                    default:
                        System.out.println("an object arrived");
                        break;
                }


            }
            // cleanup
            socket.close();
            System.out.println("Client " + CID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
