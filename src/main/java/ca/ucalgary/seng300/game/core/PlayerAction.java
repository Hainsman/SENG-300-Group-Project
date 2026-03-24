package ca.ucalgary.seng300.game.core;

public abstract class PlayerAction {
    private final String playerID;
    private final String actionType;
    private final ActionPayload actionPayload;

    protected PlayerAction(String playerID, String actionType, ActionPayload actionPayload) {
        this.playerID = playerID;
        this.actionType = actionType;
        this.actionPayload = actionPayload;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getActionType() {
        return actionType;
    }

    public ActionPayload getActionPayload() {
        return  actionPayload;
    }
}
