package ca.ucalgary.seng300.platform.messages;

import java.util.UUID;

/**
 * DTO for server -> client: current game state after a move or on join.
 * board[row][col] = "PLAYER_1" | "PLAYER_2" | "EMPTY"
 * status = "IN_PROGRESS" | "PLAYER_1_WIN" | "PLAYER_2_WIN" | "TIE"
 * Serialized with Gson for TCP wire format.
 */
public class GameStateUpdate extends Message{
//    private String sessionId;
    private String[][] board;  // 3x3 for TTT: "PLAYER_1", "PLAYER_2", "EMPTY"
    private String currentTurn; // "PLAYER_1" or "PLAYER_2"
    private String status;     // "IN_PROGRESS", "PLAYER_1_WIN", "PLAYER_2_WIN", "TIE"
    private String message;   // optional error or info (e.g. validation failure)

//    public GameStateUpdate() {}
//
//    public GameStateUpdate(String sessionId, String[][] board, String currentTurn, String status) {
//        this(sessionId, board, currentTurn, status, null);
//    }

    public GameStateUpdate(UUID globallyUniqueSessionId, String[][] board, String currentTurn, String status, String message) {
        super(globallyUniqueSessionId, GameStateUpdate.class.getSimpleName());
//        this.sessionId = sessionId;
        this.board = board;
        this.currentTurn = currentTurn;
        this.status = status;
        this.message = message;
    }

//    public String getSessionId() { return sessionId; }
//    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String[][] getBoard() { return board; }
    public void setBoard(String[][] board) { this.board = board; }
    public String getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(String currentTurn) { this.currentTurn = currentTurn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
