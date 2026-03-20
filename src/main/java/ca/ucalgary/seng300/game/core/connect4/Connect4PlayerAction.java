package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class Connect4PlayerAction extends PlayerAction {
    public Connect4PlayerAction(String playerID, Connect4ActionType actionType, Connect4ActionPayload actionPayload) {
        super(playerID, actionType, actionPayload);
    }

    @Override
    public Connect4ActionPayload getActionPayload() {
        return (Connect4ActionPayload) super.getActionPayload();
    }

    @Override
    public Connect4ActionType getActionType() {
        return (Connect4ActionType) super.getActionType();
    }
}
