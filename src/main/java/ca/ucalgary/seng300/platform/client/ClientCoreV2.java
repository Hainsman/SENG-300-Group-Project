package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerMove;
import ca.ucalgary.seng300.game.core.tictactoe.TTTRuleValidator;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.messages.Message;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;
import ca.ucalgary.seng300.platform.server.ServerCoreMultiUser;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponse;
import ca.ucalgary.seng300.ui.CentralController;
import ca.ucalgary.seng300.ui.LobbyBridge;
import ca.ucalgary.seng300.ui.LoginController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import com.google.gson.Gson;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

public class ClientCoreV2 extends ClientCore {
    public String CID;
    public String username;

    private CentralController ioMain;
    private LoginController loginController;

    public BufferedReader in;
    public PrintWriter out;

    public boolean running = false;
    public volatile char flg = 'n';

    public String gameType;
    public Player thisUser;
    public String Player1 = "X";
    public String Player2 = "O";
    private TTTRuleValidator cliEngine;
    public GameState gstate;
    private Gson gson;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile String sessionId;
    private volatile String myPlayerId;
    private volatile String mySide; // "PLAYER_1" or "PLAYER_2"
    public volatile boolean upFlag = false;
    public volatile GameState lastUpdate;
    private volatile Runnable onGameStateUpdate;

    public boolean isConnected() {
        return in != null && out != null;
    }

    /**
     * Mapped lobby slot for moves: {@code P1} = host (X), {@code P2} = guest (O). Set from {@code LOBBY_OK}.
     */
    public Player getMyNetworkRole() {
        if ("P1".equals(myPlayerId)) {
            return Player.PLAYER_1;
        }
        if ("P2".equals(myPlayerId)) {
            return Player.PLAYER_2;
        }
        return null;
    }


    public void msgSend(Object o) {
        if (!isConnected()) {
            return;
        }
        out.println(new Gson().toJson(o));
        out.flush();
    }

    public ServerResponse msgRecieve(){
        try {
            gson = new Gson();
            return gson.fromJson(in.readLine(), ServerResponse.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



    static ClientCoreV2 instance = null;
    public static ClientCoreV2 getInstance() {
        if(instance == null) {
            instance = new ClientCoreV2();
        }
        return instance;
    }

    ClientCoreV2() {
        super(CentralController.getInstance());
        ioMain = CentralController.getInstance();
        loginController = LoginController.getInstance();
        cliEngine = new TTTRuleValidator();
    }

    public GameState getLastUpdate() {
        return lastUpdate;
    }

//    public String debugBoard(TTTGameState t) {
//        String s = "";
//        for (Player[] p : t.getBoard()) {
//            for (Player pp : p) {
//                s = s + pp;
//            }
//        }
//        return s;
//    }

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
        msgSend(p);
        return true;
        //else{
        //    return false;
        //}
        //}
        //else{
        //    return false;
        //}
        //return false;
    }

    @Override
    public void run() {
        System.out.println("Starting thread");
        try {
            Platform.setImplicitExit(false);
            // Connect to the server at localhost on port 2020
            Socket socket = new Socket();
            try {
                // try to establish a connection with a 1 second timeout
                socket.setSoTimeout(1000);
                socket.connect(new InetSocketAddress("localhost", 2020));
                socket.setSoTimeout(0);
            } catch (Exception e) {
                System.out.println("Client unable to connect to game server (is PlatformCore running on port 2020?): " + e.getMessage());
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                return;
            }

            // Set up input and output streams for talking back and forth
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                System.out.println("in="+in.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Message message;


            // wait for first client response
            // if the client is closed unexpectedly readline returns null so this catches any error

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
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
}
