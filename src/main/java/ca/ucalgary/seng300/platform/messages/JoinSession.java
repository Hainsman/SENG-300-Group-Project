package ca.ucalgary.seng300.platform.messages;

import java.util.UUID;

public class JoinSession extends Message{
    private int gameHistoryID; //instance of gameID being played
    private int gameID;

    public JoinSession(UUID globallyUniqueSessionId, int gameHistoryID, int gameID) {
        super(globallyUniqueSessionId, JoinSession.class.getSimpleName());
        this.gameHistoryID = gameHistoryID;
        this.gameID = gameID;
    }
}
