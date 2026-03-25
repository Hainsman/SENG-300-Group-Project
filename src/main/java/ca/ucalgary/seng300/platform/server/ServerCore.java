package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.tictactoe.TTTActionPayload;
import ca.ucalgary.seng300.game.core.tictactoe.TTTActionType;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.game.core.tictactoe.TTTPlayerAction;
import ca.ucalgary.seng300.platform.messages.*;

import java.io.IOException;
import java.util.ArrayList;
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
    private volatile boolean running;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private Game game;

    public ServerCore(int port) {
        this.port = port;
    }

    /** Start the server on specified port. **/
    public void start() throws IOException {
        server = new ServerSocket(port);
        Thread acceptPlayers = new Thread(() -> {
            try {
                // Two players
                clients.add(acceptClient(server));
                clients.add(acceptClient(server));
            } catch (IOException _) {}
        });

        acceptPlayers.start();

        int curr = clients.size();
        while (clients.size() < 2) {
            // NOTE: please don't remove this if statement, IntelliJ just doesn't understand the vision
            if (curr != clients.size()) {
                System.out.println("Number of players online: " + clients.size());
                curr = clients.size();
            }
            Thread.onSpinWait(); // do nothing, but efficiently
        }
        running = true;
        System.out.println("Enough players joined, (" + clients.size() + "), starting match!");

        while (running) {
            game = new TTTGame();
            System.out.println(game.getGameState());
            clients.getFirst().out.println(game.getGameState());
            String line = clients.getFirst().in.readLine();
            if (line != null) {
                handleLine(line);
            }
            String line2 = clients.get(1).in.readLine();
            if (line != null) {
                handleLine(line2);
            }
        }
    }

    public ClientHandler acceptClient(ServerSocket s) throws IOException {
        ClientHandler result = new ClientHandler();
        result.socket = s.accept();
        result.in = new BufferedReader(new InputStreamReader(result.socket.getInputStream()));
        result.out = new PrintWriter(result.socket.getOutputStream(), true);
        return result;
    }

    public void stop() {
        while (server != null) {
            try {
                server.close();
                server = null;
            } catch (Exception _) {}
        }

        for (ClientHandler c : clients) {
            try {c.socket.close();} catch (IOException _) {}
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
            default: clients.getFirst().out.println("ERROR: bad request.");
        }

        return null;
    }

    /**
     * Handle create or join session request. Platform core will implement: create/join GameSessionRecord, return SessionCreated JSON.
     */
    protected String handleCreateOrJoin(CreateOrJoinSession req) {
        switch (req.getType()) {
            case "CREATE_SESSION":
                if (req.getSessionId().equals("?")) {
                    GameSessionRecord session = new GameSessionRecord();
                    String sessionID = session.getSessionId();
                    sessions.put(sessionID, session); // Look-up table
                    session.addPlayer(req.getPlayerId());

                    clients.getFirst().out.println();
                }
            case "JOIN_SESSION":
                if (req.getSessionId() == null) {
                    for (GameSessionRecord r : sessions.values()) {
                        IO.println(r.getSessionId());
                    }
                }
        }

        return null;
    }

    /**
     * Handle submit move request. Platform core will implement: resolve session, validate move, apply via game.handleAction, return GameStateUpdate JSON.
     */
    protected String handleSubmitMove(SubmitMove m) {
        game.handleAction(
                new TTTPlayerAction(m.getPlayerId(),
                TTTActionType.MOVE,
                new TTTActionPayload(m.getRow(), m.getCol())
        ));
        return null;
    }
}
