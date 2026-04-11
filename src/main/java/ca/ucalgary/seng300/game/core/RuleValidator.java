package ca.ucalgary.seng300.game.core;

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

}
