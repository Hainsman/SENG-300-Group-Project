package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TTTGameUpdateTest {

    private TTTGameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new TTTGameState();
    }

    @Test
    // Tests if applying an action actually updates the board properly
    void applyActionUpdatesBoard() {

        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE.toString(), 1, 1);

        gameState.applyAction(action);

        assertSame(Player.PLAYER_1, gameState.getBoard()[1][1]);
    }

    @Test
    // Tests if applying an action changes player's turn
    void applyActionChangesTurn() {

        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE.toString(), 0, 0);

        gameState.applyAction(action);

        assertEquals(Player.PLAYER_2, gameState.getPlayerTurn());
    }

}
