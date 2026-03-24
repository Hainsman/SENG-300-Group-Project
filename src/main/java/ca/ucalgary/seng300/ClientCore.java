package ca.ucalgary.seng300;

import ca.ucalgary.seng300.game.core.ActionType;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.tictactoe.TTTActionPayload;
import ca.ucalgary.seng300.game.core.tictactoe.TTTRuleValidator;
import ca.ucalgary.seng300.platform.client.clientChannel;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.ui.CentralController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// debug Server first, then as many clients as you want
// the server will close once the last client has sent "exit"

/*
Documentation Stuff

The way a TCP connection works, you create a dedicated channel between two IP addresses
and then send stuff down it. The problem we have here is that the read function is a blocking
function in java, for keyboard, GUI(probably) and most importantly for net.Socket .

TCP is full duplex and is safe for multiple threads to be read/writing to it at once, but
best practice is to try and limit it to one read and write thread at a time.
ClientCore can send a message, Sock can receive a message.
PlatformCore has 2 types of listener thread and sends messages
platformChannel listens to one specific client.

 */
public class ClientCore {
    public String CID;
    public String username;
    public clientChannel ClIO;

    public boolean running = false;
    public volatile char flg = 'n';

    public String gameType;
    private TTTRuleValidator cliEngine;
    public GameState gstate;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile String sessionId;
    private volatile String myPlayerId;
    private volatile String mySide; // "PLAYER_1" or "PLAYER_2"
    public volatile  boolean upFlag = false;
    public volatile GameStateUpdate lastUpdate;
    private volatile Runnable onGameStateUpdate;

    public boolean connect() {
        ClIO = new clientChannel(this);
        ClIO.start();
        // wait for something to happen
        while(flg == 'n');
        return running;
    }
    public void disconnect(){
        ClIO.msgSend("exit");
        ClIO.interrupt();
    }
    public boolean submitMove(int row, int col){
        if(gameType == "TTT"){
            PlayerAction p = new PlayerAction(CID, "MOVE" , new TTTActionPayload(row, col)) {
            } ;
            if(cliEngine.validateMove(gstate,p)){
                ClIO.msgSend(p);
                return true;
            }
            else{
                return false;
            }
        }
        return false;
    }
    public GameStateUpdate submitMoveSync(int row, int col) {
        // STUB: platform core will implement
        return null;
    }


    public static void main(String[] args) {
        new ClientCore();

    }
}
