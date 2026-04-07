package ca.ucalgary.seng300.platform.server;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Holds one game and the playerId -> Player mapping for a single game session.
 */
public class GameSessionRecord {
    private final String sessionId;
    private final Game game;
    private final Map<String, Player> playerIdToPlayer = new HashMap<>();
    private int playerCount = 0;

    public GameSessionRecord(String type, platformChannel host) {
        this.sessionId = UUID.randomUUID().toString();
        if(type.equals("TTT")){
            this.game = new TTTGame();
        }
        else{
            this.game = new C4Game();
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public Game getGame() {
        return game;
    }

    public Map<String, Player> getPlayerIdToPlayer() {
        return new HashMap<>(playerIdToPlayer);
    }

    /** Returns true if a second player was added (session now full and game started). */
    public boolean addPlayer(String playerId) {
        if (playerCount >= 2) return false;
        if (playerCount == 0) {
            playerIdToPlayer.put(playerId, Player.PLAYER_1);
        } else {
            playerIdToPlayer.put(playerId, Player.PLAYER_2);
            game.startGame(null, null);
            game.setPlayerIdToPlayer(playerIdToPlayer);
        }
        playerCount++;
        return playerCount == 2;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
