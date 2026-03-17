package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.*;

public class TTTGame implements Game {

    private GameState gameState = new TTTGameState();
    private RuleValidator validator = new TTTRuleValidator();

    @Override
    public GameState startGame(Object Player1, Object Player2) {
        return null;
    }

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
