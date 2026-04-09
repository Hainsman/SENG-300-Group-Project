package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTRuleValidator;
import ca.ucalgary.seng300.platform.messages.C4WireState;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.messages.TTTWireState;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.lobby.LobbyBridge;
import ca.ucalgary.seng300.ui.util.Navigation;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class ClientCore extends Thread{
    public String CID;
    public String username;

    private ObjectInputStream servRead;
    private ObjectOutputStream servSend;

    private CentralController ioMain;

    public boolean running = false;
    public volatile char flg = 'n';

    public String gameType;
    public Player thisUser;
    public String Player1 = "X";
    public String Player2 = "O";
    private TTTRuleValidator cliEngine;
    public GameState gstate;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile String sessionId;
    private volatile String myPlayerId;
    private volatile String mySide; // "PLAYER_1" or "PLAYER_2"
    public volatile  boolean upFlag = false;
    public volatile GameState lastUpdate;
    private volatile Runnable onGameStateUpdate;

    // Authentication
    private volatile String sessionToken;
    private volatile int userId;
    private volatile String loginResult;
    private final Object loginLock = new Object();
    
    public boolean isConnected() {
        return servSend != null && servRead != null;
    }

    public boolean login(String username, String password) {
        if (!isConnected()) {
            return false;
        }
        loginResult = null;
        sysMSG loginMsg = new sysMSG("LOGIN", username, password);
        try {
            servSend.writeObject(loginMsg);
            servSend.flush();
            synchronized (loginLock) {
                loginLock.wait(5000); // 5 second timeout
                if (loginResult != null && loginResult.startsWith("SUCCESS")) {
                    String[] parts = loginResult.split("\\|", 4);
                    if (parts.length >= 4) {
                        sessionToken = parts[1];
                        this.username = parts[2];
                        userId = Integer.parseInt(parts[3]);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getUserId() {
        return userId;
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
        //if(gameType == "TTT"){
        //if(lastUpdate == null || lastUpdate.getPlayerTurn().equals(thisUser)){
            String pid = (myPlayerId != null && !myPlayerId.isBlank())
                    ? myPlayerId
                    : ((CID != null && !CID.isBlank()) ? CID : "player");
            TTTPlayerMove p = new TTTPlayerMove(pid, "MOVE" , row, col);
            //if(cliEngine.validateMove(gstate,p)){
            try{
                servSend.writeObject(p);
                servSend.flush();
                return true;
            } catch (IOException e) {
                return false;
            }
            //else{
            //    return false;
            //}
        //}
        //else{
        //    return false;
        //}
        //return false;
    }

    public void msgSend(Object o) {
        if (!isConnected())
            return;

        try{
            servSend.writeObject(o);
            servSend.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameStateUpdate submitMoveSync(int row, int col) {
        submitMove(row,col);
        return null;
    }
    public ClientCore(CentralController c){
        ioMain = c;
        cliEngine = new TTTRuleValidator();
    }

    public GameState getLastUpdate() {
        return lastUpdate;
    }

    /** Clear per-match client state before creating/joining a new lobby. */
    public void resetMatchState() {
        lastUpdate = null;
        myPlayerId = null;
        mySide = null;
        sessionId = null;
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

    private void onGameStateReceived(GameState g) {
        System.out.println("a gamestate arrived");
        lastUpdate = g;
        if (g instanceof TTTGameState tttState) {
            System.out.println(debugBoard(tttState));
        }
        Platform.runLater(() -> {
            if (g instanceof TTTGameState tttState && ioMain != null && ioMain.tttPage != null) {
                ioMain.tttPage.refreshFromGameStateUpdate(tttState);
                System.out.println("Ui update triggered");
            } else if (g instanceof C4GameState c4State && ioMain != null && ioMain.connect4 != null) {
                ioMain.connect4.refreshFromGameStateUpdate(c4State);
            }
            LobbyBridge.notifyGameState();
        });
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            Platform.setImplicitExit(false);
            // Connect to the server at localhost on port 2020
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
                } catch (ClassNotFoundException e) {
                    System.err.println("Network: unknown object type from server (rebuild PlatformCore from the same sources as the client): " + e.getMessage());
                    break;
                } catch (RuntimeException e) {
                    System.err.println("Network: failed to read server message.");
                    e.printStackTrace();
                    break;
                }

                if (message instanceof String s && "exit".equals(s)) {
                    break;
                }

                switch(message){
                    case sysMSG sm:
                        switch (sm.getType()){
                            case "LOGIN_SUCCESS":
                                synchronized (loginLock) {
                                    loginResult = "SUCCESS|" + sm.getFieldA() + "|" + sm.getFieldB() + "|" + sm.getPayload();
                                    loginLock.notify();
                                }
                                break;
                            case "LOGIN_FAIL":
                                synchronized (loginLock) {
                                    loginResult = "FAIL|" + sm.getFieldB();
                                    loginLock.notify();
                                }
                                break;
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
                            case "GMJN":
                                upFlag = true;
                                {
                                    String screen = sm.getFieldA();
                                    if (screen != null && !screen.isBlank()) {
                                        final String fxml = screen;
                                        Platform.runLater(() -> {
                                            LobbyBridge.clearLobbyUiCallbacks();
                                            CentralController.moveTo(Navigation.getFromFxml(fxml));
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
                            case "LOBBY_ROSTER": {
                                Object pl = sm.getPayload();
                                CentralController.applyLobbyRosterPayload(pl != null ? pl.toString() : "");
                                LobbyBridge.notifyPartnerJoined();
                                break;
                            }
                            case "MATCH_CHAT":
                                if (ioMain != null) {
                                    String sender = sm.getFieldA();
                                    String body = sm.getFieldB();
                                    Platform.runLater(() -> ioMain.addIncomingMatchChat(sender, body));
                                }
                                break;
                            case "REMATCH_REQUEST":
                                if (ioMain != null) {
                                    Platform.runLater(() -> ioMain.onRematchRequested(sm.getFieldA()));
                                }
                                break;
                            case "REMATCH_ACCEPTED":
                                if (ioMain != null) {
                                    Platform.runLater(() -> ioMain.onRematchAccepted(sm.getFieldA()));
                                }
                                break;
                            case "REMATCH_DECLINED":
                                if (ioMain != null) {
                                    Platform.runLater(() -> ioMain.onRematchDeclined(sm.getFieldA()));
                                }
                                break;
                            case "MOVE_INVALID":
                                if (ioMain != null) {
                                    String reason = sm.getFieldB() != null ? sm.getFieldB() : "Invalid move";
                                    Platform.runLater(() -> ioMain.showMoveError(reason));
                                }
                                break;
                            default:
                                break;
                        }
                        break;


                    case TTTWireState tw:
                        onGameStateReceived(tw.toGameState());
                        break;
                    case C4WireState cw:
                        onGameStateReceived(cw.toGameState());
                        break;
                    case GameState g:
                        onGameStateReceived(g);
                        break;
                    case chatMessage c:
                        // CHAT SYSTEM UI COMPONENT
                        break;
                    //case matchMaking m:

                    default:
                        System.out.println("an object arrived");
                        break;
                }


            }
            // cleanup
            if (!socket.isClosed()) {
                socket.close();
            }
            System.out.println("Client " + CID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            servSend = null;
            servRead = null;
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
    /*public static void main(String[] args) {
        try {
            ClientCore c = new ClientCore(new CentralController()); // Start the server
            c.start();
            System.out.println("client launched");
            sleep(1000);
            c.submitMove(1,1);
            System.out.println("gst sent");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
