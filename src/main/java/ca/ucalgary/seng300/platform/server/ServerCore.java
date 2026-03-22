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
        running = true;


        while (running) {
            String line = in.readLine();
            if (line != null) {
                handleLine(line);
            }
        }
    }

    public void stop() {
        while (server != null) {
            try {
                server.close();
                server = null;
            } catch (Exception _) {} // noop!
        }

        while (client != null) {
            try {
                client.close();
                client = null;
            } catch (Exception _) {}
        }
        running = false;
    }

    /**
     * Handle one line of input from a client; return one JSON line to send back, or null.
     * Platform core will implement: parse CreateOrJoinSession or SubmitMove, dispatch to handleCreateOrJoin / handleSubmitMove, return response.
     */
    protected String handleLine(String line) {
        String[] spl = line.split(" ");
        switch (line.split(" ")[0]) {
            case "CREATE_SESSION", "JOIN_SESSION": handleCreateOrJoin(new CreateOrJoinSession(spl[0], spl[1], spl[2], spl[3]));
            case "MOVE": handleSubmitMove(new SubmitMove(spl[1], spl[2], Integer.parseInt(spl[3]), Integer.parseInt(spl[4])));
            default: out.println("ERROR: bad request.");
        }

        // TODO: JSON line?
        return null;
    }

    /**
     * Handle create or join session request. Platform core will implement: create/join GameSessionRecord, return SessionCreated JSON.
     */
    protected String handleCreateOrJoin(CreateOrJoinSession req) {
        // TODO: delete, testing
        {
            System.out.println("DEBUG: successfully got create or join request.");
            System.out.println(req.getGameType());
            System.out.println(req.getType());
            System.out.println(req.getPlayerId());
            System.out.println(req.getSessionId());
        }
        return null;
    }

    /**
     * Handle submit move request. Platform core will implement: resolve session, validate move, apply via game.handleAction, return GameStateUpdate JSON.
     */
    protected String handleSubmitMove(SubmitMove m) {
        // TODO: delete, testing
        {
            System.out.println("DEBUG: successfully got move.");
            System.out.println(m.getCol());
            System.out.println(m.getRow());
            System.out.println(m.getPlayerId());
            System.out.println(m.getSessionId());
        }
        return null;
    }
}
