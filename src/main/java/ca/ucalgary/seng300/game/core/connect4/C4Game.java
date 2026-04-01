package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.*;

import ca.ucalgary.seng300.game.core.Game;
import ca.ucalgary.seng300.game.core.GameState;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.PlayerAction;
import ca.ucalgary.seng300.game.core.RuleValidator;
import ca.ucalgary.seng300.game.core.tictactoe.TTTGameState;
import ca.ucalgary.seng300.platform.messages.GameStateUpdate;
import ca.ucalgary.seng300.platform.server.platformChannel;

import java.util.Map;

public class C4Game implements Game {

    private GameState gameState = new C4GameState();
    private RuleValidator validator = new C4RuleValidator();
    public platformChannel player1 = null;
    public platformChannel player2 = null;

    public boolean userJoin(platformChannel p){
        if(player1 == null){
            player1 = p;
            return true;
        }
        else if(player2 == null){
            player2 = p;
            return true;
        }
        else{
            return false;
        }
    }

    public void tempReset(){
        gameState = new TTTGameState();
    }
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
