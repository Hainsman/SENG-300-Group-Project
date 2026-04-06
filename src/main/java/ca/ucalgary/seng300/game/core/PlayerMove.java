package ca.ucalgary.seng300.game.core;

import java.io.Serializable;

public abstract class PlayerMove implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String playerID;
    private final String actionType;
    private final int row;
    private final int col;

    protected PlayerMove(String playerID, String actionType, Integer row, Integer col) {
        this.playerID = playerID;
        this.actionType = actionType;
        this.row = row;
        this.col = col;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getActionType() {
        return actionType;
    }
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
}
