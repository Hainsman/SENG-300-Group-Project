package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.RuleValidator;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.server.platformChannel;

import java.util.Map;

public class TTTGame implements Game {

    private GameState gameState = new TTTGameState();
    private final TTTRuleValidator validator = new TTTRuleValidator();

    public void tempReset(){
        gameState = new TTTGameState();
    }

    /** Declares {@code forfeitingPlayer} lost; opponent wins. No-op if game already over. */
    public void forfeit(Player forfeitingPlayer) {
        if (gameState instanceof TTTGameState gs) {
            gs.applyForfeit(forfeitingPlayer);
        }
    }
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
        return gameState.getState() != ca.ucalgary.seng300.game.core.State.IN_PROGRESS;
    }

    /** Optional: set mapping for turn enforcement (playerId -> PLAYER_1/PLAYER_2). When null, validator does not check turn. */
    //public void setPlayerIdToPlayer(Map<String, Player> map) {
    //    validator.setPlayerIdToPlayer(map);
    //}
}
