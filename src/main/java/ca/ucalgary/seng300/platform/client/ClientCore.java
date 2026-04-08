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
import java.util.function.Consumer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    public Player myPlayer;
    public volatile  boolean upFlag = false;
    public volatile TTTGameState lastTT;
    public volatile C4GameState lastC4;
    private volatile Consumer<sysMSG> authResponseHandler;
    private volatile Consumer<sysMSG> newUserResponseHandler;
    private volatile Consumer<sysMSG> moveResponseHandler;
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
        Object p = null;
        switch(gameType) {
            case GameType.TIC_TAC_TOE:
                p = new TTTPlayerAction(myPlayer, "MOVE", row, col);
                break;
            case GameType.CONNECT4:
                p = new C4PlayerAction(myPlayer, "MOVE" , row, col);
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
            servSend.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientCore(CentralController c){
        ioMain = c;
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

    public void setChatMessageHandler(Consumer<chatMessage> handler) {
        chatMessageHandler = handler;
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
                                myPlayer = getMyNetworkRole();
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
                    case TTTGameState t:
                        //lastTT = t;
                        System.out.println("TTT update sent"+t.getBoard()[0][0].toString());
                        System.out.println(t.getBoard());
                        Platform.runLater(() -> {
                            ioMain.tttPage.refreshFromGameStateUpdate(t);
                        });
                        break;
                    case C4GameState c:
                        lastC4 = c;
                        System.out.println("C4 update sent"+c.getBoard()[0][0].toString());
                        Platform.runLater(() -> {
                            ioMain.connect4.refreshFromGameStateUpdate(c);
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
