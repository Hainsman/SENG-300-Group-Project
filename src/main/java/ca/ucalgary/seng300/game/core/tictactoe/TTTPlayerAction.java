package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class TTTPlayerAction extends PlayerAction {
    /**
     *
     * @param playerID
     * @param actionType Type of action being submitted. Either MOVE or RESIGN type.
     * @param actionPayload A TTTActionPayload must be created with row and column information to submit a move.
     */
    public TTTPlayerAction(String playerID, TTTActionType actionType, TTTActionPayload actionPayload) {
        super(playerID, actionType, actionPayload);
    }

    @Override
    public TTTActionPayload getActionPayload() {
        return (TTTActionPayload) super.getActionPayload();
    }

    @Override
    public TTTActionType getActionType() {
        return (TTTActionType) super.getActionType();
    }
}
