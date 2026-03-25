package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.constants.TypeSession;

import java.util.UUID;

/**
 * Client -> server: request to create a new game session or join an existing one.
 * type = "CREATE_SESSION" | "JOIN_SESSION"
 * For JOIN_SESSION, sessionId must be set. For CREATE_SESSION, sessionId can be null.
 */
public class CreateOrJoinSession extends Message{
    private TypeSession typeSession;      // "CREATE_SESSION" or "JOIN_SESSION"
    private int gameID;

    public CreateOrJoinSession(TypeSession typeSession, UUID globallyUniqueSessionId, int gameID) {
        super(globallyUniqueSessionId, CreateOrJoinSession.class.getSimpleName());
        this.typeSession = typeSession;
        this.gameID = gameID;
    }

    public TypeSession getTypeSession() { return typeSession; }
    public void setTypeSession(TypeSession typeSession) { this.typeSession = typeSession; }
    public int getGameType() { return gameID; }
    public void setGameType(int gameID) { this.gameID = gameID; }
}
