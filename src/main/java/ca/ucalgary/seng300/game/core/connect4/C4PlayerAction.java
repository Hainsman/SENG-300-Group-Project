package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class C4PlayerAction extends PlayerAction {
    public C4PlayerAction(String playerID, C4ActionType actionType, int row, int col) {
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
    public C4ActionType getActionType() {
        return (C4ActionType) super.getActionType();
    }
}
