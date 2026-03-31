package ca.ucalgary.seng300.game.core.tictactoe;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TTTGameUpdateTest {

    @Test
    // Tests is applying an action actually updates the board
    void applyActionUpdatesBoard() {

        TTTGameState gameState = new TTTGameState();

        TTTActionPayload payload = new TTTActionPayload(1, 1);
        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE, payload);

        assertTrue(gameState.applyAction(action));
    }

    @Test
    // Tests if applying an action changes player's turn
    void applyActionChangesTurn() {

        TTTGameState gameState = new TTTGameState();

        TTTActionPayload payload = new TTTActionPayload(0, 0);
        TTTPlayerAction action = new TTTPlayerAction("player1", TTTActionType.MOVE, payload);

        gameState.applyAction(action);

        assertEquals(Player.PLAYER_2, gameState.getPlayerTurn());
    }

}
