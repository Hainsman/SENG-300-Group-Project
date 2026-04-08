package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
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
import java.util.UUID;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class ClientCore extends Thread{
    public String CID;
    public UUID uuid;
    public String username;

    private ObjectInputStream servRead;
    private ObjectOutputStream servSend;

    private CentralController ioMain;

    //public volatile char flg = 'n';
    public String UItype;
    public GameType gameType;

    //private final ExecutorService executor = Executors.newSingleThreadExecutor();
    //private volatile String sessionId;
    private volatile String myPlayerId;
    public volatile  boolean upFlag = false;
    public volatile GameState lastUpdate;
    //private volatile Runnable onGameStateUpdate;
    
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
        Object p = null;
        switch(gameType) {
            case GameType.TIC_TAC_TOE:
                p = new TTTPlayerAction(pid, "MOVE", row, col);
                break;
            case GameType.CONNECT4:
                p = new C4PlayerAction(pid, "MOVE" , row, col);
                break;
            default:
                return false;
        }

        //
        if(p != null) {
            msgSend(p);
            return true;
        }
        return false;
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

    public ClientCore(CentralController c){
        ioMain = c;
    }

    public GameState getLastUpdate() {
        return lastUpdate;
    }


    private void onGameStateReceived(GameState g) {
        System.out.println("a gamestate arrived");
        lastUpdate = g;
        if (g instanceof TTTGameState tttState) {
            //System.out.println(debugBoard(tttState));
        }
        Platform.runLater(() -> {
            ioMain.tttPage.refreshFromGameStateUpdate((TTTGameState) g);
            /*if (g instanceof TTTGameState tttState && ioMain != null && ioMain.isGameOn()) {

                System.out.println("Ui update triggered");
            } else if (g instanceof C4GameState c4State && ioMain != null && ioMain.isGameOn()) {
                ioMain.connect4.refreshFromGameStateUpdate(c4State);
            }*/
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
                            case "AUTH":
                                System.out.println("loginreply got");
                                Platform.runLater(() -> {
                                    ioMain.loginPage.loginReply(sm);
                                });
                                break;
                            case "NEWU":
                                Platform.runLater(() -> {
                                    ioMain.newUserPage.newReply(sm);
                                });
                                break;
                            case "GMJN":
                                String screen = sm.getFieldA();
                                if (screen != null && !screen.isBlank()) {
                                    final String fxml = screen;
                                    Platform.runLater(() -> {
                                        LobbyBridge.clearLobbyUiCallbacks();
                                        CentralController.moveTo(Navigation.getFromFxml(fxml));
                                    });
                                }
                                break;
                            case "LOBBY_HOST":
                                Platform.runLater(() -> {
                                    ioMain.hostLobbyPage.hostLobby(sm.getFieldA(),sm.getFieldB());
                                });
                                break;
                            case "LOBBY_JOIN":
                                Platform.runLater(() -> {
                                    ioMain.joinLobbyPage.lobbyJoin(sm.getFieldA(),sm.getFieldB());
                                });
                                break;
                            case "START_LOBBY":
                                System.out.println("start got");
                                myPlayerId = sm.getFieldA();
                                Platform.runLater(() -> {
                                    ioMain.mainLobbyPage.gameStart();
                                });
                                break;
                            case "LOBBY_ROSTER": {
                                Object pl = sm.getPayload();
                                System.out.println("lobby got");
                                CentralController.applyLobbyRosterPayload(pl != null ? pl.toString() : "");
                                Platform.runLater(() -> {
                                    ioMain.mainLobbyPage.refreshPlayersFromState();
                                });
                                break;
                            }
                            default:
                                break;
                        }
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
