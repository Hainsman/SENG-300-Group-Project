package ca.ucalgary.seng300.platform.messages;

/**
 * Client -> server: request to create a new game session or join an existing one.
 * type = "CREATE_SESSION" | "JOIN_SESSION"
 * For JOIN_SESSION, sessionId must be set. For CREATE_SESSION, sessionId can be null.
 */
public class CreateOrJoinSession {
    private String type;      // "CREATE_SESSION" or "JOIN_SESSION"
    private String sessionId; // required for JOIN
    private String playerId;  // this client's player id
    private String gameType;

    public CreateOrJoinSession() {}

    public CreateOrJoinSession(String type, String playerId) {
        this(type, null, playerId, "TICTACTOE");
    }

    public CreateOrJoinSession(String type, String sessionId, String playerId, String gameType) {
        this.type = type;
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.gameType = gameType != null ? gameType : "TICTACTOE";
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }
}
