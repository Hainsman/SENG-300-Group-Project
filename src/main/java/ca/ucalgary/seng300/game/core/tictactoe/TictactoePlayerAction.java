package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.PlayerAction;

public class TictactoePlayerAction extends PlayerAction {
    public TictactoePlayerAction(String playerID, TictactoeActionType actionType, TictactoeActionPayload actionPayload) {
        super(playerID, actionType, actionPayload);
    }

    @Override
    public TictactoeActionPayload getActionPayload() {
        return (TictactoeActionPayload) super.getActionPayload();
    }

    @Override
    public TictactoeActionType getActionType() {
        return (TictactoeActionType) super.getActionType();
    }
}
