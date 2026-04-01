package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.*;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponse;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponseGameRoom;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;
import ca.ucalgary.seng300.platform.utils.Helper;
import ca.ucalgary.seng300.platform.constants.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.google.gson.Gson;

/**
 * STUB: TCP server. Matchmaking is in progress; platform core will implement real networking.
 * Same API is kept so ServerMain and platform core can wire up later.
 * <p>
 * When implemented: listen for clients, handle CreateOrJoinSession and SubmitMove,
 * reply with SessionCreated and GameStateUpdate (one JSON line per message).
 */
public class ServerCoreMultiUser{
    private Game game;
    private final int port;
    private List<GameRoom> gameRooms;
    private ServerSocket serverSocket;
    private volatile boolean running;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private ExecutorService threadPool;
    private static final int DEFAULT_PORT = 2020;
    private static final int MAX_THREADS = 10;


    private static ServerCoreMultiUser instance;

    public static ServerCoreMultiUser getInstance() {
        if(instance == null)
            instance = new ServerCoreMultiUser(DEFAULT_PORT, MAX_THREADS);
        return instance;
    }
    ServerCoreMultiUser(int port, int maxThreads) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(maxThreads);
    }

    public void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port + " with thread pool");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Submit client handling to thread pool
                    threadPool.submit(new ClientHandler(clientSocket));

                } catch (SocketException e) {
                    if (running) {
                        System.err.println("Socket error: " + e.getMessage());
                    }
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Recieved Request: "+ inputLine);
                    ServerResponse resp = instance.handleLine(inputLine);
                    System.out.println("Sending Response: " + resp);
                    out.println(new Gson().toJson(resp)); //send response back
                }

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (IOException | InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    /**
     * Handle one line of input from a client; return one JSON line to send back, or null.
     * Platform core will implement: parse CreateOrJoinSession or SubmitMove, dispatch to handleCreateOrJoin / handleSubmitMove, return response.
     */
    public ServerResponse handleLine(String line) {
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
//            case SubmitMove sm: return handleSubmitMove(sm);
//            case GameStateUpdate gsu:break;
            default:
                // TODO: report error
        }
        return ServerResponse.Fail("Error message invalid");
    }

    // handles authentication of users
    protected ServerResponse handleAuthenticate(Authenticate auth){

        UserStore userStore = UserStore.getInstance();
        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
        List<User> listOfUsers= userStore.findByUserName(auth.getUsername());
        String incomingUsername = auth.getUsername();
        String incomingPassword = Helper.hashPassword(auth.getPassword(), Constants.HASH_ALGORITHM);

        if (listOfUsers.isEmpty())
            return ServerResponse.Fail("Error Username not found");

        if (incomingUsername.equals(listOfUsers.getFirst().getlogin()) && incomingPassword.equals(listOfUsers.getFirst().getPasswordHash())){
            sessionHistoryStore.create(new SessionHistory(1, listOfUsers.getFirst().getID(), auth.getGloballyUniqueSessionId()));
            return ServerResponse.Success("User successfully authenticated");
        }
        return ServerResponse.Fail("Error username or password incorrect");
    }

    protected ServerResponse handleDeAuthenticate(){
        return null;
    }

    private ServerResponse handleCreateGameRoom(CreateGameRoom cs, User user) {
        // check user don't have room with this game id already
        if(gameRooms.stream().anyMatch(e -> e.getOwner().getID() == user.getID()))
            return ServerResponse.Fail("User already in gameRood");

        var resp = new ServerResponseGameRoom();
        // create room
        gameRooms.add(new GameRoom(cs.getGameType(), new ArrayList<User>(List.of(user)), user, UUID.randomUUID()));
        resp.gameRoom = gameRooms.getLast();
        return resp;
    }

    /**
     * Handle create or join session request. Platform core will implement: create/join GameSessionRecord, return SessionCreated JSON.
     */
    private ServerResponse handleJoinGameRoom(JoinGameRoom js, User user) {
        //check if user is present in any gameRoom
        if(gameRooms.stream().anyMatch(e -> e.getOwner().getID() == user.getID()))
            return ServerResponse.Fail("User already in game room");

        var resp = new ServerResponseGameRoom();
        resp.gameRoom = gameRooms
                .stream()
                .filter(e -> e.getGameRoomID().equals(js.getGloballyUniqueSessionId()))
                .findFirst().orElse(null);

        if (resp.gameRoom == null)
            return ServerResponseGameRoom.Fail("Error game Room non existent");

        resp.gameRoom.getParticipants().add(user);
        resp.status = ServerResponse.Status.SUCCESS;
        return resp;
    }

    private ServerResponse handleSubmitMove(SubmitMove m) {
        return null;
    }
}