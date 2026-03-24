package ca.ucalgary.seng300.game.core;

/**
 * Client-side game interface that provides local validation logic
 * to reduce invalid actions sent to the server.
 */
public interface ClientGame {

    /**
     * Validates a player action against the current game state using
     * client-side rule validation before sending it to the server.
     *
     * @param action The action to validate
     * @return True if the action is valid and can be sent to the server
     */
    boolean validate(PlayerAction action);

    /**
     * Returns the current client-side game state.
     *
     * @return The current GameState
     */
    GameState getGameState();
}
