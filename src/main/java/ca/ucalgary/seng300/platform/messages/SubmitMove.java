package ca.ucalgary.seng300.platform.messages;

import ca.ucalgary.seng300.platform.constants.Game;

import java.util.UUID;

/**
 * DTO for client -> server: request to submit a move in a game session.
 * Serialized with Gson for TCP wire format.
 */
public class SubmitMove extends Message{

    private int row;
    private int col;
    private int gameID;

    public SubmitMove(UUID globallyUniqueSessionId, int row, int col, int gameID) {
        super(globallyUniqueSessionId, SubmitMove.class.getSimpleName());
        this.row = row;
        this.col = col;
        this.gameID = gameID;
    }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    public int getGameID() { return gameID; }
    public void setGameID(int gameID) { this.gameID = gameID; }
}
