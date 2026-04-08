package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.PlayerMove;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.io.*;
import java.net.Socket;

public class platformChannel extends Thread {
    private Socket socket;
    private String lobby;

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
    public void setLobby(String lob, int gameNumber){
        lobby = lob;
    }

    public String getLobby() {
        return lobby;
    }

    private void handleSys(sysMSG sm) {
        switch (sm.getType()) {
            case "AUTH":
                cliSend(serv.authenticate(sm.getFieldA(), sm.getFieldB(), this));
                break;
            case "NEWU":
                cliSend(serv.createAccount(sm.getFieldA(), sm.getFieldB(), sm.getPayload()));
                break;
            case "HOST_LOBBY":
                String code = sm.getFieldA();
                int gt;
                try {
                    gt = sm.getFieldB() != null ? Integer.parseInt(sm.getFieldB()) : Game.TICK_TACK_TOE;
                } catch (NumberFormatException e) {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST", "Hosting a lobby failed: invalid game type."));
                    break;
                }
                String lobbyPayload = sm.getPayload() != null ? sm.getPayload() : "";
                boolean ok = serv.onHostLobby(code, gt, this, lobbyPayload);
                if (ok) {
                    cliSend(new sysMSG("LOBBY_OK", code.toUpperCase(), "HOST"));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST", "Hosting a lobby failed."));
                }
                break;
            case "JOIN_LOBBY":
                int requestedGameId = 0;
                try {
                    if (sm.getFieldB() != null && !sm.getFieldB().isBlank()) {
                        requestedGameId = Integer.parseInt(sm.getFieldB());
                    }
                } catch (NumberFormatException e) {
                    requestedGameId = 0;
                }
                System.out.println("JOIN_LOBBY received for code " + sm.getFieldA() + " gameId=" + requestedGameId);
                if (serv.onJoinLobby(sm.getFieldA(), requestedGameId, this, sm.getPayload())) {
                    cliSend(new sysMSG("LOBBY_OK", sm.getFieldA(), "JOIN"));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "JOIN", "Joining a lobby failed. Ensure the code exists and both players selected the same game."));
                }
                break;
            case "CREATE_GAME":
                if (!serv.onCreateGame(sm.getFieldA(), this)) {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST", "Starting the game failed."));
                }
                break;
            //case "FORFEIT" -> serv.onForfeit(this);

            default:
                break;
        }
    }

    @Override
    public void run() {
        try{
            outServ = new ObjectOutputStream(socket.getOutputStream());
            inCli = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        while (!interrupted()) {
            Object message;
            try {
                message = inCli.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if (message instanceof String str && "exit".equals(str)) {
                break;
            }

            switch (message) {
                case PlayerAction p -> {
                    System.out.println("player action arrived");
                    serv.handleMove(lobby,this, p);
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

        try{
            socket.close();
        }catch (Exception e){
            System.out.println("socket already closed");
        }

        System.out.println("Client " + ID + " " + socket.getInetAddress() + " has disconnected.");
    }





//    private Socket socket;
//    private String lobby;
//    private String gameQueue;
//    private String gameType;
//    private String gameID;
//
//    private PlatformCore serv;
//    private ObjectOutputStream outServ;
//    private ObjectInputStream inCli;
//    public  String ID;
//
//
//    public platformChannel(Socket socket, PlatformCore serv) {
//        this.socket = socket;
//        this.serv = serv;
//    }
//
//    public void cliSend (Object o){
//        try{
//            outServ.writeObject(o);
//            outServ.flush();
//            outServ.reset();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void handleSys(sysMSG sm) {
//        switch (sm.getType()) {
//            case "HOST_LOBBY" -> {
//                String code = sm.getFieldA();
//                String gt = sm.getFieldB() != null ? sm.getFieldB() : "TTT";
//                this.gameType = gt;
//                boolean ok = serv.onHostLobby(code, gt, this);
//                if (ok) {
//                    cliSend(new sysMSG("LOBBY_OK", code.toUpperCase(), "HOST"));
//                } else {
//                    cliSend(new sysMSG("LOBBY_ERR", "create", "Could not create lobby (code may already be in use)."));
//                }
//            }
//            case "JOIN_LOBBY" -> {
//                String code = sm.getFieldA();
//                boolean ok = serv.onJoinLobby(code, this);
//                if (ok) {
//                    cliSend(new sysMSG("LOBBY_OK", code.toUpperCase(), "JOIN"));
//                } else {
//                    cliSend(new sysMSG("LOBBY_ERR", "join", "No lobby with that code (check with host or try again)."));
//                }
//            }
//            case "GMJN" -> serv.relayToGuestIfHost(this, sm);
//            case "reset" -> serv.tempGameReset();
//            default -> { }
//        }
//    }
//
//    public void setGameType(String gameType) {
//        this.gameType = gameType;
//    }
//
//    public String getGameType() {
//        return gameType;
//    }
//
//    public void setGameID(String gameID) {
//        this.gameID = gameID;
//    }
//
//    public String getGameID() {
//        return gameID;
//    }
//
//    @Override
//    public void run() {
//        try {
//            outServ = new ObjectOutputStream(socket.getOutputStream());
//            inCli = new ObjectInputStream(socket.getInputStream());
//
//            gameID = "test";
//            gameType = "TTT";
//
//            while (!interrupted()) {
//                Object message = inCli.readObject();
//
//                if (message instanceof String str && "exit".equals(str)) {
//                    break;
//                }
//
//                switch (message) {
//                    case PlayerMove p -> {
//                        System.out.println("player action arrived");
//                        serv.gameEvent(gameID, gameType, p);
//                    }
//                    case sysMSG s -> handleSys(s);
//                    case chatMessage cm -> {
//                        if (serv.chatservers != null && serv.chatservers.contains(cm.getServ())) {
//                            serv.sendMult(cm);
//                        }
//                    }
//                    default -> System.out.println("def " + message);
//                }
//            }
//
//            socket.close();
//            System.out.println("Client " + ID + " " + socket.getInetAddress() + " has disconnected.");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
