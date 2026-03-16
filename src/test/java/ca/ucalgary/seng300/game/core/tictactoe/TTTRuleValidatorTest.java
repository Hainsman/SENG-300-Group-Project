package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TTTRuleValidatorTest {

    private TTTRuleValidator validator;
    private TTTGameState gameState;

    @Test
    void isValidMove() {

        validator = new TTTRuleValidator();
        gameState = new TTTGameState();



    }
}
