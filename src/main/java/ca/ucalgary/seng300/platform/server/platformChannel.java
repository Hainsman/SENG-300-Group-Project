package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.PlatformCore;
import ca.ucalgary.seng300.game.core.GameType;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.platform.messages.chatMessage;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.io.*;
import java.net.Socket;

public class platformChannel extends Thread {
    private Socket socket;
    private String lobby;
    private GameType gameType;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setLobby(String lob, GameType game){
        lobby = lob;
        gameType = game;
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
                String gt = sm.getFieldB() != null ? sm.getFieldB() : "TTT";
                String lobbyPayload = sm.getPayload() != null ? sm.getPayload().toString() : "";
                boolean ok = serv.onHostLobby(code, gt, this, lobbyPayload);
                if (ok) {
                    cliSend(new sysMSG("LOBBY_OK","HOST", code.toUpperCase()));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "HOST", "Could not create lobby (code may already be in use)."));
                }
                break;
            case "JOIN_LOBBY":
                //String code = sm.getFieldA();
                //boolean ok = serv.onJoinLobby(sm.getFieldA(), this);
                if (serv.onJoinLobby(sm.getFieldA(), this, sm.getPayload())) {
                    cliSend(new sysMSG("LOBBY_OK", sm.getFieldA(), "JOIN"));
                } else {
                    cliSend(new sysMSG("LOBBY_ERR", "join", "No lobby with that code (check with host or try again)."));
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
}
