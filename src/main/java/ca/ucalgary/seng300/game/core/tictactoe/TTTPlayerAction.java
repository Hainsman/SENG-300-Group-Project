package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class TTTPlayerAction extends PlayerAction {
    public TTTPlayerAction(String playerID, TTTActionType actionType, int row, int col) {
        super(playerID, actionType, row, col);
    }

    @Override
    public int getRowValue() {
        return super.getRowValue();
    }

    @Override
    public int getColValue() {
        return super.getColValue();
    }


    @Override
    public TTTActionType getActionType() {
        return (TTTActionType) super.getActionType();
    }
}
