package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TTTRuleValidator extends RuleValidator {

    /** Optional: when set, turn is enforced (action's playerID must map to current turn). When null, turn is not checked. */
    private Map<String, Player> playerIdToPlayer = null;

    public void setPlayerIdToPlayer(Map<String, Player> map) {
        this.playerIdToPlayer = map;
    }

    public boolean validateMove(GameState gameState, PlayerAction action) {
        //Attempt to cast gameState and action to TTT types
        TTTPlayerAction tttAction;
        TTTGameState tttGameState;
        try {
            tttAction = (TTTPlayerAction) action;
            tttGameState = (TTTGameState) gameState;
        } catch (ClassCastException e) {
            System.out.println("Incorrect type of PlayerAction or GameState: " + e.getMessage());
            return false;
        }

        //reject an action that is:
        //1. not the right player's turn (when playerIdToPlayer is set)
        //2. the game is not in progress
        boolean isCorrectTurn = (playerIdToPlayer == null)
                || tttGameState.getPlayerTurn().equals(playerIdToPlayer.get(action.getPlayerID()));
        boolean isInProgress = tttGameState.getState() == State.IN_PROGRESS;
        if (!isCorrectTurn || !isInProgress) {
            System.out.println("Player action out of turn or game was not in progress");
            return false;
        }

        //Checks for move type of action
        if ("MOVE".equals(action.getActionType())) {
            int row = tttAction.getActionPayload().row();
            int col = tttAction.getActionPayload().col();
            //Reject move if row or col is out of range
            if (!(0 <= row && row <= 2 && 0 <= col && col <= 2)) {
                System.out.println("PlayerAction row or column out of range");
                return false;
            }
            //Reject move if trying to overwrite existing token
            if (gameState.getBoard()[row][col] != Player.EMPTY) {
                System.out.println("PlayerAction attempted to overwrite tile");
                return false;
            }
        }

        return true;
    }

    public List<PlayerAction> getValidActions(GameState gameState) {
        //Check for correct gameState type
        TTTGameState tttGameState;
        try {
            tttGameState = (TTTGameState) gameState;
        } catch (ClassCastException e) {
            System.out.println("Incorrect type of GameState: " + e.getMessage());
            return null;
        }

        List<PlayerAction> validActions = new ArrayList<>();
        if (tttGameState.getState() != State.IN_PROGRESS) {
            return validActions;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameState.getBoard()[i][j] == Player.EMPTY) {
                    TTTActionPayload newPayload = new TTTActionPayload(i, j);
                    TTTPlayerAction newAction = new TTTPlayerAction("temp", "MOVE", newPayload);
                    validActions.add(newAction);
                }
            }
        }
        return validActions;
    }
}
