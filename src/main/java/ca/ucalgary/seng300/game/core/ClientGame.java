package ca.ucalgary.seng300.game.core;

import java.util.List;

/**
 * Client-side game interface that maintains a local copy of game state
 * and validation logic. Allows the client to check move validity locally
 * before sending actions to the server, reducing unnecessary server calls
 * and invalid action submissions.
 */
public interface ClientGame {

    /**
     * Validates a player action against the local game state.
     * Should be called before sending any action to the server.
     *
     * @param action The action to validate locally
     * @return True if the action is valid and should be sent to the server
     */
    boolean validate(PlayerAction action);

    /**
     * Returns the list of actions that would pass local validation
     * given the current game state. Useful for UI hints or
     * restricting player input to only valid options.
     *
     * @return List of locally valid actions
     */
    List<PlayerAction> getValidActions();

    /**
     * Updates the local game state to match the server's authoritative state.
     * Should be called whenever the client receives a state update from the server.
     *
     * @param gameState The server-provided game state to sync locally
     */
    void updateLocalState(GameState gameState);

    /**
     * Returns the current local copy of the game state.
     *
     * @return The local GameState
     */
    GameState getLocalState();
}
