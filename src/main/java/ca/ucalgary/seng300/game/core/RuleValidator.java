package ca.ucalgary.seng300.game.core;

import java.util.List;

public abstract class RuleValidator {

    /**
     * Returns true if the submitted move is valid given the current state of the game.
     * May be updated later to return more information about invalid moves.
     *
     * @param gameState The current GameState to reference for move validation
     * @param action The action to verify against the GameState
     * @return True if valid
     */
    public abstract boolean validateMove(GameState gameState, PlayerAction action);

    /**
     * Returns a generated list of PlayerActions that would be considered valid based on the current GameState
     *
     * @param gameState The current GameState.
     * @return List of valid actions
     */
    public abstract List<PlayerAction> getValidActions(GameState gameState);
}
