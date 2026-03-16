package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TTTRuleValidatorTest {

    private TTTRuleValidator validator;
    private TTTGameState gameState;

    @Test
    // Tests inserting a piece at the center of an empty board
    void isValidMove() {

        validator = new TTTRuleValidator();
        gameState = new TTTGameState();

        TTTActionPayload payload = new TTTActionPayload(1, 1);
        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE, payload);

        boolean isValid = validator.validateMove(gameState, action);

        assertTrue(isValid, "Move should be valid on an empty board.");
    }

    @Test
    // Detects when a piece is inserted outside the bounds of the board
    void isOutOfBounds() {
        validator = new TTTRuleValidator();
        gameState = new TTTGameState();

        TTTActionPayload payload = new TTTActionPayload(3, 3);
        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE, payload);

        boolean isValid = validator.validateMove(gameState, action);

        assertFalse(isValid, "Inserting piece outside board should return False.");
    }


}
