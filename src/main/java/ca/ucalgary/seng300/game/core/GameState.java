package ca.ucalgary.seng300.game.core;

import java.io.Serializable;

public interface GameState extends Serializable {
    Player[][] board = null;
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

    //-- Accessors --
    Player[][] getBoard();

    Player getPlayerTurn();

    State getState();
}
