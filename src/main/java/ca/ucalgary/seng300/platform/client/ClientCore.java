package ca.ucalgary.seng300.platform.client;

import ca.ucalgary.seng300.platform.messages.*;
import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;

/**
 * STUB: Client for the game server. Matchmaking is in progress; platform core will implement
 * real networking. Same API is kept so UI and platform core can wire up later.
 * <p>
 * When implemented: connect to ServerCore, send CreateOrJoinSession and SubmitMove,
 * receive SessionCreated and GameStateUpdate, notify UI on JavaFX thread via callback.
 */
public class ClientCore {

    private final String host;
    private final int port;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile String sessionId;
    private volatile String myPlayerId;
    private volatile String mySide; // "PLAYER_1" or "PLAYER_2"
    private volatile GameStateUpdate lastUpdate;
    private volatile Runnable onGameStateUpdate;
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;

    public ClientCore(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /** Callback run on FX thread when a GameStateUpdate is received. Platform core will invoke when real networking is in place. */
    public void setOnGameStateUpdate(Runnable r) {
        this.onGameStateUpdate = r;
    }

    public String getSessionId() { return sessionId; }
    public String getMyPlayerId() { return myPlayerId; }
    public String getMySide() { return mySide; }
    public GameStateUpdate getLastUpdate() { return lastUpdate; }

    /**
     * Connect to server.
     * @return Boolean: true if successfully connected, false if not.
     */
    public boolean connect() {
        try { server = new Socket(host, port); } catch (Exception _ ) {
            System.err.println("ERROR: couldn't connect to server.");
            server = null; // NOTE(Dylan): Just in case ;-)
            return false;
        }
        try { out = new PrintWriter(server.getOutputStream(), true); } catch (Exception _) {
            System.err.println("ERROR: couldn't set up stream for outputting to server.");
            server = null; // NOTE(Dylan): Just in case ;-)
            out = null;
            return false;
        }

        return false;
    }

    /** Closes connection. */
    public void disconnect() {
        while (true) {
            try {
                server.close();
                break;
            } catch (Exception _) {} // noop!
        }
        executor.shutdownNow();
    }

    /**
     * Create a new session. Blocks until response. Returns SessionCreated or null on error. Sets sessionId and mySide.
     * STUB: returns null; platform core will implement.
     */
    public SessionCreated createSession(String playerId) {
        // STUB: platform core will implement (send CreateOrJoinSession, receive SessionCreated)
        return null;
    }

    /**
     * Join an existing session. Blocks until response. Returns SessionCreated or null on error.
     * STUB: returns null; platform core will implement.
     */
    public SessionCreated joinSession(String sessionIdToJoin, String playerId) {
        // STUB: platform core will implement
        return null;
    }

    /**
     * Submit a move. When implemented: send to server and run onGameStateUpdate on FX thread when response arrives.
     */
    public void submitMove(int row, int col) {
        out.println(String.format("%d %d", row, col));
        // NOTE(Dylan): I have no idea how to do the onGameStateUpdate callback thing...
        // I think I'm just going to focus on single-threading for now?
    }

    /**
     * Synchronous submit: blocks until GameStateUpdate is received. Returns the update or null.
     * STUB: returns null; platform core will implement.
     */
    public GameStateUpdate submitMoveSync(int row, int col) {
        // STUB: platform core will implement
        return null;
    }
}
