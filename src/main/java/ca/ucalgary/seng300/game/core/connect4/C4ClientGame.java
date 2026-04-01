package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.*;

import java.util.List;

/**
 * Client-side Connect 4 validation layer. Maintains a local copy of
 * the game state and validates moves before they are sent to the server.
 */
public class C4ClientGame implements ClientGame {

    private C4GameState localState;
    private final C4RuleValidator validator;

    public C4ClientGame() {
        this.localState = new C4GameState();
        this.validator = new C4RuleValidator();
    }

    @Override
    public boolean validate(PlayerAction action) {
        return validator.validateMove(localState, action);
    }

    @Override
    public List<PlayerAction> getValidActions() {
        return validator.getValidActions(localState);
    }

    @Override
    public void updateLocalState(GameState gameState) {
        this.localState = (C4GameState) gameState;
    }

    @Override
    public GameState getLocalState() {
        return localState;
    }
}
