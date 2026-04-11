package ca.ucalgary.seng300.platform.session;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import ca.ucalgary.seng300.game.core.connect4.C4Game;
import ca.ucalgary.seng300.game.core.connect4.C4GameState;
import ca.ucalgary.seng300.game.core.connect4.C4PlayerAction;

public class Connect4Session {
    private final C4Game game = new C4Game();

    public Connect4Session() {
        startNewGame();
    }

    public void startNewGame() {
        game.startGame("PLAYER_1", "PLAYER_2");
    }

    public boolean submitMove(int col) {
        if (game.getGameState().getState() != State.IN_PROGRESS) {
            return false;
        }
        C4GameState state = (C4GameState) game.getGameState();
        String playerId = state.getPlayerTurn().toString();
        C4PlayerAction action = new C4PlayerAction(playerId, "MOVE", 0, col);
        game.handleAction(action);
        return game.wasLastMoveValid();
    }

    public C4GameState getGameState() {
        return (C4GameState) game.getGameState();
    }

    public Player getTurn() {
        return game.getGameState().getPlayerTurn();
    }

    public State getState() {
        return game.getGameState().getState();
    }

    public boolean isGameOver() {
        return game.isGameOver();
    }
}
