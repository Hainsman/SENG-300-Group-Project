package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.*;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    private List<GameRoom> gameRooms;
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
        System.out.println("Number of players online: " + clients.size());
        while (clients.size() < 2) {
            if (curr != clients.size()) {
                System.out.println("Number of players online: " + clients.size());
                curr = clients.size();
            }
            Thread.onSpinWait(); // do nothing, but efficiently
        }
        running = true;
        System.out.println("Enough players joined, (" + clients.size() + "), starting match!");
        game = new TTTGame();

        while (running) {
            for (ClientHandler c : clients) {
                running = move((TTTGame) game, c);
            }
        }
        // Need to repeat one more time to send game status to everyone
        for (ClientHandler c : clients) {
            move((TTTGame) game, c);
        }

    }

    /**
     * Handles one client's move.
     * @return Whether the game should continue or not.
     */
    public boolean move(TTTGame game, ClientHandler c) throws IOException {
        System.out.println(game.getGameState());
        c.out.println(game.getGameState());
        if (game.getGameState().getState() == State.TIE) {
            c.out.println("TIE");
            return false;
        }
        if (game.getGameState().getState() == State.PLAYER_1_WIN) {
            c.out.println("P1_WIN");
            return false;
        }
        if (game.getGameState().getState() == State.PLAYER_2_WIN) {
            c.out.println("P2_WIN");
            return false;
        }
        c.out.println("IN_PROGRESS");

        boolean valid = false;
        while (!valid) {
            String line = c.in.readLine();
            if (line != null) {
                handleLine(line); // Modifies game state
            }
            valid = game.valid;
            if (!valid) {
                System.out.println("DEBUG: player sent invalid move.");
                c.out.println("INVALID");
            }
        }
        c.out.println("VALID");
        return true;
    }

    public void printToAll(String message) {
        for (ClientHandler c : clients) {
            c.out.println(message);
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
    public Response handleLine(String line) {
        Response resp = new Response();
        Message message = Message.fromJson(line);

        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
        List<SessionHistory> session = sessionHistoryStore.findSessionByUUID(message.getGloballyUniqueSessionId());

        if(session.isEmpty() && message instanceof Authenticate auth)
            return handleAuthenticate(auth); // TODO: handle edge cases

        int userId = session.getFirst().getUserId();
        User user = UserStore.getInstance().get(userId);
        switch (message){
            case CreateGameRoom cs: return handleCreateGameRoom(cs, user);
            case JoinGameRoom js: return handleJoinGameRoom(js, user);
            case SubmitMove sm: return handleSubmitMove(sm);
            case GameStateUpdate gsu:break;
            default:
                // TODO: report error
        }
        resp.status = Response.Status.FAIL;
        resp.errors = new String[] {"Message Invalid"};
        return resp;
    }



    // handles authentcation of users
    protected Response handleAuthenticate(Authenticate auth){
        Response resp = new Response(); //user already registered

        UserStore userStore = UserStore.getInstance();
        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
        List<User> listOfUsers= userStore.findByUserName(auth.getUsername());
        String incomingUsername = auth.getUsername();
        String incomingPassword = auth.getPassword();

        if (listOfUsers.isEmpty()){
            resp.status = Response.Status.FAIL;
            resp.errors = new String[] {"Error password or username Incorrect"};
            return resp;
        }

        if (incomingUsername.equals(listOfUsers.getFirst().getlogin()) && incomingPassword.equals(listOfUsers.getFirst().getPasswordHash())){
            sessionHistoryStore.create(new SessionHistory(1, listOfUsers.getFirst().getID(), auth.getGloballyUniqueSessionId()));
            resp.status = Response.Status.SUCCESS;
            resp.errors = new String[]{""};
            return resp;
        }

        resp.status = Response.Status.FAIL;
        resp.errors = new String[]{"Error password or Username Incorrect"};

        return resp;
    }

    protected Response handleDeAuthenticate(){
        return null;
    }

    private Response handleCreateGameRoom(CreateGameRoom cs, User user) {
        Response resp = new Response();
        // check user don't have room with this game id already

        if(gameRooms.stream().anyMatch(e -> e.getOwner().getID() == user.getID())){
            resp.status = Response.Status.FAIL;
            resp.errors = new String[]{"Error User already present in Game Room"};
        }

        // create room
        gameRooms.add(new GameRoom(cs.getGameType(), new ArrayList<User>(List.of(user)), user, UUID.randomUUID()));
        resp.status = Response.Status.SUCCESS;
        return resp;
    }



    /**
     * Handle create or join session request. Platform core will implement: create/join GameSessionRecord, return SessionCreated JSON.
     */

    private Response handleJoinGameRoom(JoinGameRoom js, User user) {
        Response resp = new Response();

        //check if user is present in any gameRoom
        if(gameRooms.stream().anyMatch(e -> e.getOwner().getID() == user.getID())){
            resp.status = Response.Status.FAIL;
            resp.errors = new String[]{"Error User already present in Game Room"};
        }

        GameRoom joinGameRoom = (GameRoom) gameRooms.stream().filter(e -> e.getGameRoomID().equals(js.getGloballyUniqueSessionId()));
        joinGameRoom.getParticipants().add(user);
        resp.status = Response.Status.SUCCESS;
        resp.errors = new String[]{"User Successfully joined GameRoom"};
        return resp;
    }

    private Response handleSubmitMove(SubmitMove m) {
        return null;
    }

    public static class Response {
        public enum Status {
            SUCCESS,
            FAIL
        }
        public Status status;
        public String[] errors;

        static public Response Fail(String message) {
            var res = new Response();
            res.status = Status.FAIL;
            res.errors = new String[]{message};
            return  res;
        }
    }
}








