package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.RuleValidator;

import java.util.List;

public class TTTRuleValidator extends RuleValidator {

    public boolean validateMove(GameState gameState, PlayerAction action) {
        //reject an action that is:
        //1. not the right game type
        //2. not the right player's turn
        boolean isTTT = action.getClass().equals(TTTPlayerAction.class);
        boolean isCorrectTurn = true; //action.getPlayerID().equals(gameState.getTurn());
        boolean isInProgress = true; //gameState.getState()
        if (!isTTT || !isCorrectTurn) {
            return false;
        }

        return false;
    }

    public List<PlayerAction> getValidActions(GameState gameState) {
        return List.of();
    }
}
