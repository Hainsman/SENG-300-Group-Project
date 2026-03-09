package ca.ucalgary.seng300.game.core;

import java.util.List;

public interface RuleValidator {

    /**
     * Returns true if the submitted move is valid given the current state of the game.
     * May be updated later to return more information about invalid moves.
     *
     * @param state The current GameState to reference for move validation
     * @param action The action to verify against the GameState
     * @return
     */
    boolean validateMove(GameState state, PlayerAction action);

    /**
     * Returns a generated list of PlayerActions that would be considered valid based on the current GameState
     *
     * @param state The current GameState.
     * @return
     */
    List<PlayerAction> getValidActions(GameState state);
}
