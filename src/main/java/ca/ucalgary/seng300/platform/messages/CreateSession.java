package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.platform.constants.TypeSession;

import java.util.UUID;

public class CreateSession extends Message{
    private int gameID;

    public CreateSession(UUID globallyUniqueSessionId, int gameID) {
        super(globallyUniqueSessionId, CreateSession.class.getSimpleName());
        this.gameID = gameID;
    }
    public int getGameType() { return gameID; }
}
