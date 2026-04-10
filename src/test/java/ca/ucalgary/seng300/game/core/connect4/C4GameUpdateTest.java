package ca.ucalgary.seng300.game.core.connect4;

import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class C4GameUpdateTest {

    @Test
    void applyActionDropsPieceToBottomRow() {
        C4GameState state = new C4GameState();
        C4PlayerAction action = new C4PlayerAction(Player.PLAYER_1.toString(), "MOVE", 0, 2);

        assertTrue(state.applyAction(action));
        assertEquals(Player.PLAYER_1, state.getBoard()[5][2]);
    }

    @Test
    void applyActionAlternatesTurnAfterMove() {
        C4GameState state = new C4GameState();
        C4PlayerAction action = new C4PlayerAction(Player.PLAYER_1.toString(), "MOVE", 0, 0);

        state.applyAction(action);
        assertEquals(Player.PLAYER_2, state.getPlayerTurn());
    }
}
