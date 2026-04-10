package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.platform.server.platformChannel;

import java.util.Map;

public class TTTGame implements Game {

    private GameState gameState = new TTTGameState();
    private final TTTRuleValidator validator = new TTTRuleValidator();
    public platformChannel player1 = null;
    public platformChannel player2 = null;

    public boolean valid;

    @Override
    public GameState startGame(Object Player1, Object Player2) {
        this.gameState = new TTTGameState();
        return gameState;
    }

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
        return gameState.getState() != ca.ucalgary.seng300.game.core.State.IN_PROGRESS;
    }

    /** Optional: set mapping for turn enforcement (playerId -> PLAYER_1/PLAYER_2). When null, validator does not check turn. */
    public void setPlayerIdToPlayer(Map<String, Player> map) {
        validator.setPlayerIdToPlayer(map);
    }
}
