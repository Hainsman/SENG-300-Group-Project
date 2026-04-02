package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.SessionHistory;
import ca.ucalgary.seng300.platform.domain.User;
import ca.ucalgary.seng300.platform.messages.*;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponse;
import ca.ucalgary.seng300.platform.serverreponse.ServerResponseGameRoom;
import ca.ucalgary.seng300.platform.store.SessionHistoryStore;
import ca.ucalgary.seng300.platform.store.UserStore;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.google.gson.Gson;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    private static final int DEFAULT_PORT = Constants.PLATFORM_GAME_PORT;
    private static final int MAX_THREADS = 10;
    //password hashing algorithm
    public static final String HASH_ALGORITHM = "SHA-256";
    // regex for verifying if email is generally compliant in RFC 5322 and RFC 5321 (Note this does not cover all cases but will cover 90 to 95% of all emails compliant in RFC standards)
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


    private static ServerCoreMultiUser instance;

    public static ServerCoreMultiUser getInstance() {
        if(instance == null)
            instance = new ServerCoreMultiUser(DEFAULT_PORT, MAX_THREADS);
        return instance;
    }
    ServerCoreMultiUser(int port, int maxThreads) {
        this.port = port;
        this.gameRooms = new ArrayList<>();
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

        if(session.isEmpty() && message instanceof CreateAccount createAccount)
            return handleCreateAccount(createAccount); // TODO: handle edge cases

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

    private ServerResponse handleCreateAccount(CreateAccount createAccount) {
        UserStore userStore = UserStore.getInstance();
        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
        List<User> listOfUsers= userStore.findByUserName(createAccount.getUsername());
        int newSessionId = sessionHistoryStore.assignNewID();
        int newUserId = userStore.assignNewID();
        String incomingUsername = createAccount.getUsername();
        String incomingEmail = createAccount.getEmail();
        String incomingPassword = hashPassword(createAccount.getPassword(), HASH_ALGORITHM);

        if (!listOfUsers.isEmpty())
            return ServerResponse.Fail("Error username exists please try again");

        if (!isEmailValid(incomingEmail, EMAIL_REGEX))
            return ServerResponse.Fail("Error email is invalid please try again");

        sessionHistoryStore.create(new SessionHistory(newSessionId, newUserId, createAccount.getGloballyUniqueSessionId()));
        userStore.create(new User(newUserId, incomingUsername, incomingEmail, incomingPassword));
        return ServerResponse.Success("Successfully created new user");
    }

    // handles authentication of users
    protected ServerResponse handleAuthenticate(Authenticate auth){
        UserStore userStore = UserStore.getInstance();
        SessionHistoryStore sessionHistoryStore = SessionHistoryStore.getInstance();
        List<User> listOfUsers= userStore.findByUserName(auth.getUsername());
        int newSessionId = sessionHistoryStore.assignNewID();
        String incomingUsername = auth.getUsername();
        String incomingPassword = hashPassword(auth.getPassword(), HASH_ALGORITHM);

        if (listOfUsers.isEmpty())
            return ServerResponse.Fail("Error Username not found");

        if (incomingUsername.equals(listOfUsers.getFirst().getlogin()) && incomingPassword.equals(listOfUsers.getFirst().getPasswordHash())){
            sessionHistoryStore.create(new SessionHistory(newSessionId, listOfUsers.getFirst().getID(), auth.getGloballyUniqueSessionId()));
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

    // helper methods used for server logic
    private static String hashPassword(String password, String algorithm){
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(password.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] hash){
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash){
            hexString.append(String.format("%X", b));
        }
        return hexString.toString();
    }

    private static boolean isEmailValid(String email, String emailRegex){
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || email.isEmpty()){
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}