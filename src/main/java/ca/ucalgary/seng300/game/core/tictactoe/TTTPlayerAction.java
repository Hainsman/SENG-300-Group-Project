package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class TTTPlayerAction extends PlayerAction {
    public TTTPlayerAction(String playerID, String actionType, TTTActionPayload actionPayload) {
        super(playerID, actionType, actionPayload);
    }

    @Override
    public TTTActionPayload getActionPayload() {
        return (TTTActionPayload) super.getActionPayload();
    }

    @Override
    public String getActionType() {
        return super.getActionType();
    }
}
