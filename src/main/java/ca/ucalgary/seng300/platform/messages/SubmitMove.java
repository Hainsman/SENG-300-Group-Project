package ca.ucalgary.seng300.platform.messages;

/**
 * DTO for client -> server: request to submit a move in a game session.
 * Serialized with Gson for TCP wire format.
 */
/*
public class SubmitMove {
    private String sessionId;
    private String playerId;
    private int row;
    private int col;
    private String gameType;

    public SubmitMove() {}

    public SubmitMove(String sessionId, String playerId, int row, int col) {
        this(sessionId, playerId, row, col, "TICTACTOE");
    }

    public SubmitMove(String sessionId, String playerId, int row, int col, String gameType) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.row = row;
        this.col = col;
        this.gameType = gameType != null ? gameType : "TICTACTOE";
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }
}*/
