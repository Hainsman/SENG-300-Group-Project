package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TTTGameStateTest {

    @Test
    // Tests is applying an action actually updates the board
    void applyActionUpdatesBoard() {

        TTTGameState gameState = new TTTGameState();

        TTTPlayerAction action = new TTTPlayerAction("player1", "MOVE", 1, 1);

        assertTrue(gameState.applyAction(action));
    }

    @Test
    // Tests if applying an action changes player's turn
    void applyActionChangesTurn() {

        TTTGameState gameState = new TTTGameState();

        TTTPlayerAction action = new TTTPlayerAction("player1", "MOVE", 0, 0);

        gameState.applyAction(action);

        assertEquals(Player.PLAYER_2, gameState.getPlayerTurn());
    }

}
