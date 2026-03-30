package ca.ucalgary.seng300.game.core;

public abstract class PlayerAction {
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
