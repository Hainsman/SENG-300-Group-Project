package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.platform.messages.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;
import java.net.*;

/**
 * STUB: TCP server. Matchmaking is in progress; platform core will implement real networking.
 * Same API is kept so ServerMain and platform core can wire up later.
 * <p>
 * When implemented: listen for clients, handle CreateOrJoinSession and SubmitMove,
 * reply with SessionCreated and GameStateUpdate (one JSON line per message).
 */
public class ServerCore {

    private final int port;
    private final Map<String, GameSessionRecord> sessions = new ConcurrentHashMap<>();
    private ServerSocket server;
    private Socket client;
    private volatile boolean running;
    private BufferedReader in;
    private PrintWriter out;

    public ServerCore(int port) {
        this.port = port;
    }

    /** Start the server on specified port. **/
    public void start() throws IOException {
        server = new ServerSocket(port);
        client = server.accept();

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);

        { // Probably remove this later, just a test.
            String message = in.readLine();
            System.out.println("Success! Client said: " + message);
        }

        running = true;
    }

    /** Stop the server. STUB: no-op; platform core will implement. */
    public void stop() {
        running = false;
    }

    /**
     * Handle one line of input from a client; return one JSON line to send back, or null.
     * Platform core will implement: parse CreateOrJoinSession or SubmitMove, dispatch to handleCreateOrJoin / handleSubmitMove, return response.
     */
    protected String handleLine(String line) {
        // STUB: platform core will implement (WireFormat.fromJsonCreateOrJoin, fromJsonSubmitMove, route to handleCreateOrJoin, handleSubmitMove)
        return null;
    }

    /**
     * Handle create or join session request. Platform core will implement: create/join GameSessionRecord, return SessionCreated JSON.
     */
    protected String handleCreateOrJoin(CreateOrJoinSession req) {
        // STUB: platform core will implement
        return null;
    }

    /**
     * Handle submit move request. Platform core will implement: resolve session, validate move, apply via game.handleAction, return GameStateUpdate JSON.
     */
    protected String handleSubmitMove(SubmitMove m) {
        // STUB: platform core will implement
        return null;
    }
}
