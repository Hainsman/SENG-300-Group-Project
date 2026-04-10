package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class C4RuleValidatorTest {

    @Test
    void validateMoveAcceptsPlayableColumnOnCurrentTurn() {
        C4RuleValidator validator = new C4RuleValidator();
        C4GameState state = new C4GameState();
        C4PlayerAction action = new C4PlayerAction(Player.PLAYER_1.toString(), "MOVE", 0, 3);
        assertTrue(validator.validateMove(state, action));
    }

    @Test
    void validateMoveRejectsOutOfTurnAction() {
        C4RuleValidator validator = new C4RuleValidator();
        C4GameState state = new C4GameState();
        C4PlayerAction action = new C4PlayerAction(Player.PLAYER_2.toString(), "MOVE", 0, 3);
        assertFalse(validator.validateMove(state, action));
    }
}
