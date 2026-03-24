package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.*;

public class C4Game implements Game {

    private GameState gameState = new C4GameState();
    private RuleValidator validator = new C4RuleValidator();

    @Override
    public GameState startGame(Object Player1, Object Player2) {
        return null;
    }

    /**
     * Handles an action submitted to the game. Must be of type C4PlayerAction.
     * Will reject invalid actions if submitted.
     * @param playerAction A PlayerAction object containing information about the player turn being submitted
     */
    @Override
    public void handleAction(PlayerAction playerAction) {
        if (validator.validateMove(gameState, playerAction)) {
            gameState.applyAction(playerAction);
        }
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public Object getGameUpdate() {
        return null;
    }

    @Override
    public boolean isGameOver() {
        return gameState.getState() != State.IN_PROGRESS;
    }
}
