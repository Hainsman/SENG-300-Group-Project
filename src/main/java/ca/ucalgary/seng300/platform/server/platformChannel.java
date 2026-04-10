package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.io.*;
import java.net.Socket;

public class platformChannel extends Thread {
    private Socket socket;
    private String lobby;
    private String gameQueue;
    private String gameType;
    private String gameID;

    private PlatformCore serv;
    private ObjectOutputStream outServ;
    private ObjectInputStream inCli;
    public  String ID;

    private String sessionToken;
    private int userId;


    public platformChannel(Socket socket, PlatformCore serv) {
        this.socket = socket;
        this.serv = serv;
    }

    public synchronized void cliSend (Object o){
        try{
            outServ.writeObject(o);
            outServ.flush();
            outServ.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSys(sysMSG sm) {
        switch (sm.getType()) {
            case "HOST_LOBBY" -> {
                String code = sm.getFieldA();
                String gt = sm.getFieldB() != null ? sm.getFieldB() : "TTT";
                this.gameType = gt;
                String lobbyPayload = sm.getPayload() != null ? sm.getPayload().toString() : "";
                boolean ok = serv.onHostLobby(code, gt, this, lobbyPayload);
                if (ok) {
                    cliSend(new sysMSG("LOBBY_OK", code.toUpperCase(), "HOST"));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "create", "Could not create lobby (code may already be in use)."));
                }
            }
            case "JOIN_LOBBY" -> {
                String code = sm.getFieldA();
                String lobbyPayload = sm.getPayload() != null ? sm.getPayload().toString() : "";
                sysMSG result = serv.onJoinLobby(code, this, lobbyPayload);
                if (result != null) {
                    cliSend(result);
                }
            }
            case "GMJN" -> serv.relayToGuestIfHost(this, sm);
            case "MATCH_CHAT" -> serv.relayMatchChat(this, sm);
            case "REMATCH_REQUEST" -> serv.relayRematchRequest(this, sm);
            case "REMATCH_RESPONSE" -> serv.onRematchResponse(this, sm);
            case "reset" -> serv.tempGameReset();
            case "FORFEIT" -> serv.onForfeit(this);
            case "LOGIN" -> {
                String username = sm.getFieldA();
                String password = sm.getFieldB();
                sysMSG result = serv.handleLogin(this, username, password);
                cliSend(result);
            }
            case "NEWU" -> {
                String username = sm.getFieldA();
                String password = sm.getFieldB();
                String email = sm.getPayload() != null ? sm.getPayload().toString() : "";
                sysMSG result = serv.handleCreateAccount(username, password, email);
                cliSend(result);
            }
            case "LOGOUT" -> serv.handleLogout(this);
            default -> { }
        }
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setSessionToken(String token) {
        this.sessionToken = token;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public void run() {
        try {
            outServ = new ObjectOutputStream(socket.getOutputStream());
            inCli = new ObjectInputStream(socket.getInputStream());

            gameID = "test";
            gameType = "TTT";

            while (!interrupted()) {
                Object message;
                try {
                    message = inCli.readObject();
                } catch (java.io.EOFException | java.io.StreamCorruptedException e) {
                    System.err.println("platformChannel: client stream ended or corrupted: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    break;
                } catch (java.io.IOException e) {
                    System.err.println("platformChannel: I/O error reading client message: " + e.getMessage());
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                } catch (RuntimeException e) {
                    System.err.println("platformChannel: bad client message (rebuild client from same sources as server): " + e.getMessage());
                    e.printStackTrace();
                    break;
                }

                if (message instanceof String str && "exit".equals(str)) {
                    break;
                }

                switch (message) {
                    case PlayerAction action -> {
                        System.out.println("player action arrived");
                        serv.gameEvent(this, gameID, gameType, action);
                    }
                    case sysMSG s -> handleSys(s);
                    case chatMessage cm -> {
                        if (serv.chatservers != null && serv.chatservers.contains(cm.getServ())) {
                            serv.sendMult(cm);
                        }
                    }
                    default -> System.out.println("def " + message);
                }
            }

            serv.handleLogout(this);
            socket.close();
            System.out.println("Client " + ID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
