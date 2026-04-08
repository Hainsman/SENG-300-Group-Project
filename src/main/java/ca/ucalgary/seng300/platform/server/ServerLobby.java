package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import static ca.ucalgary.seng300.PlatformCore.parseNameEloPayload;

public class ServerLobby {
    private String lobbyCode;
    private int gameId;
    private TTTGame tttgame;
    private C4Game c4game;
    private platformChannel host;
    private platformChannel guest;
    private String hostextra;
    private String guestextra;

    public ServerLobby(String code, int gameId, platformChannel h, String ex){
        lobbyCode = code;
        this.gameId = gameId;
        if(gameId == Game.TICK_TACK_TOE){
            c4game = null;
            tttgame = new TTTGame();
        }
        else if(gameId == Game.CONNECT_FOUR){
            tttgame = null;
            c4game = new C4Game();
        }
        host = h;
        hostextra = ex;
        host.setLobby(lobbyCode, gameId);
    }

    public void joinGuest(platformChannel h, String ex){
        guest  = h;
        guestextra = ex;
        guest.setLobby(lobbyCode, gameId);
    }

    public int getGameId() {
        return gameId;
    }

    public void rosterSend(){
        String[] h = parseNameEloPayload(hostextra);
        String[] g = parseNameEloPayload(guestextra);
        String roster = h[0] + "|" + h[1] + "|" + g[0] + "|" + g[1];
        sysMSG rosterMsg = new sysMSG("LOBBY_ROSTER", lobbyCode, "", roster);
        sendBoth(rosterMsg);
    }

    public void gameReset(){
        if(gameId == Game.CONNECT_FOUR){
            c4game = new C4Game();
            c4game.startGame(host, guest);
            sendBoth(c4game.getGameState());
        }
        else if(gameId == Game.TICK_TACK_TOE){
            tttgame = new TTTGame();
            tttgame.startGame(host, guest);
            sendBoth(tttgame.getGameState());
        }
    }

    public void gameTurn(platformChannel p, PlayerAction m){
        if(gameId == Game.CONNECT_FOUR){
            c4game.handleAction(m);
            sendBoth(c4game.getGameState());
        }
        else if(gameId == Game.TICK_TACK_TOE){
            tttgame.handleAction(m);
            sendBoth(tttgame.getGameState());
        }
    }

    public void sendBoth(Object o){
        host.cliSend(o);
        guest.cliSend(o);
    }








//    private String lobbyCode;
//    private int gameId;
//    private TTTGame tttgame;
//    private C4Game c4game;
//    private platformChannel host;
//    private platformChannel guest;
//    private String hostextra;
//    private String guestextra;
//
//    public ServerLobby(String code, String type, platformChannel h, String ex){
//        lobbyCode = code;
//        if(type.equals(GameType.TIC_TAC_TOE.toString())){
//            gameType = GameType.TIC_TAC_TOE;
//            c4game = null;
//            tttgame = new TTTGame();
//        }
//        else if(type.equals(GameType.CONNECT4.toString())){
//            gameType = GameType.CONNECT4;
//            tttgame = null;
//            c4game = new C4Game();
//        }
//        host = h;
//        hostextra = ex;
//        host.setLobby(lobbyCode,gameType);
//    }
//
//    public void joinGuest(platformChannel h, String ex){
//        guest  = h;
//        guestextra = ex;
//    }
//
//    public void rosterSend(){
//        String[] h = parseNameEloPayload(hostextra);
//        String[] g = parseNameEloPayload(guestextra);
//        String roster = h[0] + "|" + h[1] + "|" + g[0] + "|" + g[1];
//        sysMSG rosterMsg = new sysMSG("LOBBY_ROSTER", lobbyCode, "", roster);
//        sendBoth(rosterMsg);
//    }
//
//    public void gameReset(){
//        if(gameType.equals(GameType.CONNECT4)){
//            c4game = new C4Game();
//            c4game.startGame(host, guest);
//            sendBoth(c4game.getGameState());
//        }
//        else if(gameType.equals(GameType.TIC_TAC_TOE)){
//            tttgame = new TTTGame();
//            tttgame.startGame(host, guest);
//            sendBoth(tttgame.getGameState());
//        }
//    }
//
//    public void gameTurn(platformChannel p, PlayerAction m){
//        if(gameType.equals(GameType.CONNECT4)){
//            c4game.handleAction(m);
//            sendBoth(c4game.getGameState());
//        }
//        else if(gameType.equals(GameType.TIC_TAC_TOE)){
//            tttgame.handleAction(m);
//            sendBoth(tttgame.getGameState());
//        }
//    }
//
//    public void sendBoth(Object o){
//        host.cliSend(o);
//        guest.cliSend(o);
//    }
}
