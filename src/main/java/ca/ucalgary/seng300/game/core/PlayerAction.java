package ca.ucalgary.seng300.game.core;

import java.io.Serializable;

public abstract class PlayerAction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String playerID;
    private final String actionType;
    private final int row;
    private final int col;

    protected PlayerAction(String playerID, String actionType, int row, int col) {
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

    public int getRowValue() {
        return row;
    }

    public int getColValue() {
        return col;
    }
}
