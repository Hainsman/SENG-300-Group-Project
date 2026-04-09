package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.messages.sysMSG;

import java.util.HashMap;
import java.util.Map;

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
    private boolean resultRecorded;
    private platformChannel pendingRematchRequester;

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
        resultRecorded = false;
        pendingRematchRequester = null;
    }

    public void joinGuest(platformChannel h, String ex){
        guest  = h;
        guestextra = ex;
        guest.setLobby(lobbyCode, gameId);
    }

    public int getGameId() {
        return gameId;
    }

    public boolean isHost(platformChannel channel) {
        return host == channel;
    }

    public platformChannel getHost() {
        return host;
    }

    public platformChannel getGuest() {
        return guest;
    }

    public String getGameFxml() {
        if (gameId == Game.CONNECT_FOUR) {
            return "Connect4.fxml";
        }
        return "TicTacToe.fxml";
    }

    public void rosterSend(){
        String[] h = parseNameEloPayload(hostextra);
        String[] g = parseNameEloPayload(guestextra);
        String roster = h[0] + "|" + h[1] + "|" + g[0] + "|" + g[1];
        sysMSG rosterMsg = new sysMSG("LOBBY_ROSTER", lobbyCode, "", roster);
        sendBoth(rosterMsg);
    }

    public void createGame(){
        resultRecorded = false;
        pendingRematchRequester = null;
        if(gameId == Game.CONNECT_FOUR){
            c4game = new C4Game();
            c4game.startGame(host, guest);
            sendBoth(new sysMSG("GMJN", getGameFxml(), ""));
            sendBoth(c4game.getGameState());
        }
        else if(gameId == Game.TICK_TACK_TOE){
            tttgame = new TTTGame();
            tttgame.startGame(host, guest);
            Map<String, Player> turnIds = new HashMap<>();
            turnIds.put("P1", Player.PLAYER_1);
            turnIds.put("P2", Player.PLAYER_2);
            tttgame.setPlayerIdToPlayer(turnIds);
            sendBoth(new sysMSG("GMJN", getGameFxml(), ""));
            sendBoth(tttgame.getGameState());
        }
    }

    public void resetGame() {
        resultRecorded = false;
        pendingRematchRequester = null;
        if (gameId == Game.CONNECT_FOUR) {
            c4game = new C4Game();
            c4game.startGame(host, guest);
            sendBoth(c4game.getGameState());
            return;
        }
        if (gameId == Game.TICK_TACK_TOE) {
            tttgame = new TTTGame();
            tttgame.startGame(host, guest);
            Map<String, Player> turnIds = new HashMap<>();
            turnIds.put("P1", Player.PLAYER_1);
            turnIds.put("P2", Player.PLAYER_2);
            tttgame.setPlayerIdToPlayer(turnIds);
            sendBoth(tttgame.getGameState());
        }
    }

    public boolean gameTurn(platformChannel p, PlayerAction m){
        if(gameId == Game.CONNECT_FOUR){
            c4game.handleAction(m);
            return c4game.wasLastMoveValid();
        }
        else if(gameId == Game.TICK_TACK_TOE){
            tttgame.handleAction(m);
            return tttgame.wasLastMoveValid();
        }
        return false;
    }

    public Object currentGameState() {
        if (gameId == Game.CONNECT_FOUR) {
            return c4game != null ? c4game.getGameState() : null;
        }
        if (gameId == Game.TICK_TACK_TOE) {
            return tttgame != null ? tttgame.getGameState() : null;
        }
        return null;
    }

    public GameState currentTypedGameState() {
        Object state = currentGameState();
        return state instanceof GameState gameState ? gameState : null;
    }

    public boolean hasRecordedResult() {
        return resultRecorded;
    }

    public void markResultRecorded() {
        resultRecorded = true;
    }

    public boolean isParticipant(platformChannel channel) {
        return channel != null && (channel == host || channel == guest);
    }

    public Player getRole(platformChannel channel) {
        if (channel == host) {
            return Player.PLAYER_1;
        }
        if (channel == guest) {
            return Player.PLAYER_2;
        }
        return null;
    }

    public boolean isGameOver() {
        GameState state = currentTypedGameState();
        return state != null && state.getState() != State.IN_PROGRESS;
    }

    public platformChannel getOpponent(platformChannel channel) {
        if (channel == host) {
            return guest;
        }
        if (channel == guest) {
            return host;
        }
        return null;
    }

    public platformChannel getPendingRematchRequester() {
        return pendingRematchRequester;
    }

    public void setPendingRematchRequester(platformChannel pendingRematchRequester) {
        this.pendingRematchRequester = pendingRematchRequester;
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
