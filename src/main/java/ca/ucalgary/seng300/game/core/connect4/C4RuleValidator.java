package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.*;

public class C4RuleValidator extends RuleValidator {

    @Override
    public boolean validateMove(GameState gameState, PlayerAction action) {
        // Attempt to cast to C4 types
        C4PlayerAction c4Action;
        C4GameState c4GameState;
        try {
            c4Action = (C4PlayerAction) action;
            c4GameState = (C4GameState) gameState;
        } catch (ClassCastException e) {
            System.out.println("Incorrect type of PlayerAction or GameState: " + e.getMessage());
            return false;
        }

        // Reject if game is not in progress
        if (c4GameState.getState() != State.IN_PROGRESS) {
            System.out.println("Game is not in progress");
            return false;
        }

        // Reject if player is out of turn
        if(!action.getPlayerID().equals(c4GameState.getPlayerTurn().toString())) {
            System.out.println("Action played out of turn");
            return false;
        }

        if ("MOVE".equals(action.getActionType())) {
            int col = c4Action.getColValue();

            // Reject if column is out of range (0-6)
            if (col < 0 || col > 6) {
                System.out.println("Column out of range");
                return false;
            }

            // Reject if the top row of the target column is not empty
            if (gameState.getBoard()[0][col] != Player.EMPTY) {
                System.out.println("PlayerAction attempted to place a piece in a full column");
                return false;
            }
        } else {
            System.out.println("Resigning not yet supported");
            return false;
        }

        return true;
    }

}
