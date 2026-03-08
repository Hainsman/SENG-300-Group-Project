package ca.ucalgary.seng300.game.core;

public abstract class PlayerAction {
    private final String playerID;
    private final ActionType actionType;
    private final ActionPayload actionPayload;

    protected PlayerAction(String playerID, ActionType actionType, ActionPayload actionPayload) {
        this.playerID = playerID;
        this.actionType = actionType;
        this.actionPayload = actionPayload;
    }

    public String getPlayerID() {
        return playerID;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public ActionPayload getActionPayload() {
        return  actionPayload;
    }
}
