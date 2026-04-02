package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.Player;
import ca.ucalgary.seng300.game.core.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class C4GameStateTest {
    private C4GameState gameState;

    @BeforeEach
    public void setUp() {
        gameState = new C4GameState();
    }

    private void playMoves(int... columns) {
        for (int col : columns) {
            C4PlayerAction action = new C4PlayerAction(gameState.getPlayerTurn().toString(), "MOVE", -1, col);
            gameState.applyAction(action);
        }
    }

    @Test
    public void testInitialization() {
        // game starts "in progress"
        assertEquals(State.IN_PROGRESS, gameState.getState());
        // player1 starts
        assertEquals(Player.PLAYER_1, gameState.getPlayerTurn());

        Player[][] board = gameState.getBoard();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                // board has to be empty at the start
                assertEquals(Player.EMPTY, board[i][j]);
            }
        }
    }

    @Test
    public void testApplyValidAction() {
        C4PlayerAction action = new C4PlayerAction(Player.PLAYER_1.toString(), "MOVE", -1, 3);
        boolean result = gameState.applyAction(action);

        // Applying a valid action should return true
        assertTrue(result);
        // Row 5 is the bottom of the board (piece should drop to the bottom row)
        assertEquals(Player.PLAYER_1, gameState.getBoard()[5][3]);
        // should be player 2's turn now
        assertEquals(Player.PLAYER_2, gameState.getPlayerTurn());
    }

    @Test
    public void testColumnFullReturnsFalse() {
        // Fill column 0 completely (6 pieces)
        playMoves(0, 0, 0, 0, 0, 0);

        // Attempt to place a 7th piece in the same column
        C4PlayerAction action = new C4PlayerAction(gameState.getPlayerTurn().toString(), "MOVE", -1, 0);
        boolean result = gameState.applyAction(action);
        // Applying an action to a full column should return false
        assertFalse(result);
        //Game state should remain in progress
        assertEquals(State.IN_PROGRESS, gameState.getState());
    }

    @Test
    public void testHorizontalWin() {
        // P1 plays 0,1,2,3; P2 plays 0,1,2 just above them
        playMoves(
                0, 0, // P1 in 0, P2 in 0
                1, 1, // P1 in 1, P2 in 1
                2, 2, // P1 in 2, P2 in 2
                3     // P1 completes horizontal line on the bottom row
        );
        //Player 1 should win horizontally
        assertEquals(State.PLAYER_1_WIN, gameState.getState());
    }

    @Test
    public void testVerticalWin() {
        // P1 plays in col 0, P2 plays in col 1
        playMoves(
                0, 1,
                0, 1,
                0, 1,
                0     // P1 completes vertical line in column 0
        );
        //Player 1 should win vertically
        assertEquals(State.PLAYER_1_WIN, gameState.getState());
    }

}
