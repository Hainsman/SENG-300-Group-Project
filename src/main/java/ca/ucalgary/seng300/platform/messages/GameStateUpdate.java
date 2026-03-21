package ca.ucalgary.seng300.platform.messages;
import ca.ucalgary.seng300.game.core.GameState;

import java.util.UUID;

/**
 * DTO for server -> client: current game state after a move or on join.
 * board[row][col] = "PLAYER_1" | "PLAYER_2" | "EMPTY"
 * status = "IN_PROGRESS" | "PLAYER_1_WIN" | "PLAYER_2_WIN" | "TIE"
 * Serialized with Gson for TCP wire format.
 */
public class GameStateUpdate extends Message{
    private GameState gameState;  // store gameState regardless of game
    private String message;   // optional error or info (e.g. validation failure)

    public GameStateUpdate(UUID globallyUniqueSessionId, GameState gameState, String message) {
        super(globallyUniqueSessionId, GameStateUpdate.class.getSimpleName());
        this.gameState = gameState;
        this.message = message;
    }

    public GameState getGameState() { return gameState; }
    public String getMessage() { return message; }
}
