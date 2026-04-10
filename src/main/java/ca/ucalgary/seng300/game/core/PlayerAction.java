package ca.ucalgary.seng300.game.core;

import java.io.Serializable;

public abstract class PlayerAction implements Serializable {
    private final Player playerID;
    private final String actionType;
    private final int row;
    private final int col;

    protected PlayerAction(Player playerID, String actionType, int row, int col) {
        this.playerID = playerID;
        this.actionType = actionType;
        this.row = row;
        this.col = col;
    }

    public Player getPlayerID() {
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
