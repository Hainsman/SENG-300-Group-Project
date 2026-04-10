package ca.ucalgary.seng300.game.core;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestValidator extends RuleValidator {

    @Override
    public boolean validateMove(GameState gameState, PlayerAction action) {
        return action.getRowValue() >= 0 && action.getColValue() >= 0;
    }

    @Override
    public List<PlayerAction> getValidActions(GameState gameState) {
        return List.of(new TestAction("p1", "MOVE", 0, 0));
    }
}

public class RuleValidatorTest {

    @Test
    void testValidateMoveValid() {
        RuleValidator validator = new TestValidator();
        GameState state = new TestGameState();
        PlayerAction action = new TestAction("p1", "MOVE", 1, 1);

        assertTrue(validator.validateMove(state, action));
    }

    @Test
    void testValidateMoveInvalid() {
        RuleValidator validator = new TestValidator();
        GameState state = new TestGameState();
        PlayerAction action = new TestAction("p1", "MOVE", -1, 1);

        assertFalse(validator.validateMove(state, action));
    }

    @Test
    void testGetValidActions() {
        RuleValidator validator = new TestValidator();
        GameState state = new TestGameState();

        assertFalse(validator.getValidActions(state).isEmpty());
    }
}