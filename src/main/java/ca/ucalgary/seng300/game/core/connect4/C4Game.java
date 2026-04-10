package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.*;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.RuleValidator;

public class C4Game implements Game {

    private GameState gameState = new C4GameState();
    private RuleValidator validator = new C4RuleValidator();

    public boolean valid;

    @Override
    public GameState startGame(Object Player1, Object Player2) {
        this.gameState = new C4GameState();
        return gameState;
    }

    /**
     * Handles an action submitted to the game. Must be of type C4PlayerAction.
     * Will reject invalid actions if submitted.
     * @param playerAction A PlayerAction object containing information about the player turn being submitted
     */
    @Override
    public void handleAction(PlayerAction playerAction) {
        valid = validator.validateMove(gameState, playerAction);
        if (valid) {
            gameState.applyAction(playerAction);
        }
    }

    public boolean wasLastMoveValid() {
        return valid;
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public boolean isGameOver() {
        return gameState.getState() != State.IN_PROGRESS;
    }


}
