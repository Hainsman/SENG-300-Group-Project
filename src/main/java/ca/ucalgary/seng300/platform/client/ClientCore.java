package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTRuleValidator;
import ca.ucalgary.seng300.platform.messages.*;
import ca.ucalgary.seng300.platform.server.ServerCoreMultiUser;
import ca.ucalgary.seng300.ui.*;
import javafx.application.Application;
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

    //public volatile char flg = 'n';
    public String UItype;
    public String gameType;
    public TicTacToeController myTTT;
    public Connect4Controller myC4;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile String sessionId;
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
            case "TTT":
                p = new TTTPlayerMove(pid, "MOVE", row, col);
                break;
            case "C4":
                //p = new C4move(pid, "MOVE" , row, col);
                break;
            default:
                return false;
        }

        //
        if(p != null) {
            try {
                servSend.writeObject(p);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public GameStateUpdate submitMoveSync(int row, int col) {
        submitMove(row,col);
        return null;
    }
    public ClientCore(CentralController c){
        ioMain = c;
    }

    public GameState getLastUpdate() {
        return lastUpdate;
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
                            case "AUTH":
                                Platform.runLater(() -> {
                                    ioMain.loginController.loginReply(sm);
                                });
                                break;
                            case "GMJN":
                                String screen = sm.getFieldA();
                                if (screen != null && !screen.isBlank()) {
                                    final String fxml = screen;
                                    Platform.runLater(() -> {
                                        LobbyBridge.clearLobbyUiCallbacks();
                                        CentralController.moveTo(fxml);
                                    });
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
                            default:
                                break;
                        }
                        break;


                    case GameState g:
                        System.out.println("a gamestate arrived");
                        lastUpdate = (TTTGameState) g;
                        System.out.println(debugBoard((TTTGameState) g));
                        Platform.runLater(() -> {
                            if (ioMain != null && ioMain.ttt != null) {
                                ioMain.ttt.refreshFromGameStateUpdate((TTTGameState) g);
                                System.out.println("Ui update triggered");
                            }
                            LobbyBridge.notifyGameState();
                        });

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
            socket.close();
            System.out.println("Client " + CID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();

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
