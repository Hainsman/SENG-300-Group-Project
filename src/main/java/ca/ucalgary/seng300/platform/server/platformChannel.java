package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.PlayerMove;
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


    public platformChannel(Socket socket, PlatformCore serv) {
        this.socket = socket;
        this.serv = serv;
    }

    public void cliSend (Object o){
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
            case "AUTH":
                String ares = serv.authenticate(sm.getFieldA(),sm.getFieldB(),this);
                switch(ares){
                    case "N/A":
                        cliSend(new sysMSG("AUTH", "User not found"));
                        break;
                    case "#F":
                        cliSend(new sysMSG("AUTH", "Login failed, invalid username or password"));
                        break;
                    default:
                        cliSend(new sysMSG("AUTH", "Pass"));
                        ID = ares;
                        break;
                }
                break;
            case "NEWU":
                String ares = serv.authenticate(sm.getFieldA(),sm.getFieldB(),this);
                switch(ares){
                    case "N/A":
                        cliSend(new sysMSG("AUTH", "User not found"));
                        break;
                    case "#F":
                        cliSend(new sysMSG("AUTH", "Login failed, invalid username or password"));
                        break;
                    default:
                        cliSend(new sysMSG("AUTH", "Pass"));
                        ID = ares;
                        break;
                }
                break;
            case "HOST_LOBBY":
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
                boolean ok = serv.onJoinLobby(code, this, lobbyPayload);
                if (ok) {
                    cliSend(new sysMSG("LOBBY_OK", code.toUpperCase(), "JOIN"));

            case "JOIN_LOBBY":
                //String code = sm.getFieldA();
                //boolean ok = serv.onJoinLobby(sm.getFieldA(), this);
                if (serv.onJoinLobby(sm.getFieldA(), this)) {
                    cliSend(new sysMSG("LOBBY_OK", sm.getFieldA(), "JOIN"));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "join", "No lobby with that code (check with host or try again)."));
                }
            }
            case "GMJN" -> serv.relayToGuestIfHost(this, sm);
            case "reset" -> serv.tempGameReset();
            case "FORFEIT" -> serv.onForfeit(this);
            default -> { }

            case "GMJN":
                serv.relayToGuestIfHost(this, sm);
                break;
            case "reset":
                serv.tempGameReset();
                break;
            default:
                break;
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
                    case PlayerMove p -> {
                        System.out.println("player action arrived");
                        serv.gameEvent(gameID, gameType, p);
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

            socket.close();
            System.out.println("Client " + ID + " " + socket.getInetAddress() + " has disconnected.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
