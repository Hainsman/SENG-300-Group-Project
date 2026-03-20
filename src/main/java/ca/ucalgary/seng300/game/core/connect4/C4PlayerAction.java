package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class C4PlayerAction extends PlayerAction {
    public C4PlayerAction(String playerID, C4ActionType actionType, C4ActionPayload actionPayload) {
        super(playerID, actionType, actionPayload);
    }

    @Override
    public C4ActionPayload getActionPayload() {
        return (C4ActionPayload) super.getActionPayload();
    }

    @Override
    public C4ActionType getActionType() {
        return (C4ActionType) super.getActionType();
    }
}
