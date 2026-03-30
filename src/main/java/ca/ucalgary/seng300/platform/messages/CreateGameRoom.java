package ca.ucalgary.seng300.platform.messages;

import java.util.UUID;

public class CreateGameRoom extends Message{
    private int gameID;

    public CreateGameRoom(UUID globallyUniqueSessionId, int gameID) {
        super(globallyUniqueSessionId, CreateGameRoom.class.getSimpleName());
        this.gameID = gameID;
    }
    public int getGameType() { return gameID; }
}
