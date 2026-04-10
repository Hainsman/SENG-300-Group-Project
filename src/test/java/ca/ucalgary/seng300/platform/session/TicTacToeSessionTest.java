package ca.ucalgary.seng300.platform.session;
import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicTacToeSessionTest {

    private TicTacToeSession session;

    @BeforeEach
    void setUp() {
        // Initialize a fresh session before each test
        session = new TicTacToeSession();
    }

    @Test
    void testInitialState() {
        // Verify the game starts IN PROGRESS
        assertEquals(State.IN_PROGRESS, session.getState());
        // game should not be over at start
        assertFalse(session.isGameOver());
        // player1 should be the one to start
        assertEquals(Player.PLAYER_1, session.getTurn());

        // Verify the board is completely empty
        Player[][] board = session.getBoard();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                // board should be empty at the start
                assertEquals(Player.EMPTY, board[row][col]);
            }
        }
    }

    @Test
    void testValidSubmitMove() {
        // Submit a valid move for PLAYER_1
        boolean moveSuccessful = session.submitMove(0, 0);

        assertTrue(moveSuccessful);
        // board should reflect PLAYER_1's move
        assertEquals(Player.PLAYER_1, session.getBoard()[0][0]);
        // turn should switch to PLAYER_2 after a valid move
        assertEquals(Player.PLAYER_2, session.getTurn());
    }

}