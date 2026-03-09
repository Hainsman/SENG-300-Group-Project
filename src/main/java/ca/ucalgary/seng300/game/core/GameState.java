package ca.ucalgary.seng300.game.core;

import java.util.ArrayList;

public interface GameState {
    ArrayList<Player> board = new ArrayList<Player>();
    Player playerTurn = Player.PLAYER_1;
    State state = State.IN_PROGRESS;

    /**
     * Applies the payload of a PlayerAction to the current board. Does not need to check for validity of incoming move,
     * it should be assumed that moves are validated before being passed into this method.
     *
     * @param action Action to apply to board
     * @return True if action is successfully applied
     */
    boolean applyAction(PlayerAction action);

    /**
     * Returns the current board variable. Useful for validation and UI implementations.
     *
     * @return Current board state
     */
    ArrayList<Player> getBoard();
}
