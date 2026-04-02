package ca.ucalgary.seng300.game.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestMove extends PlayerMove {
    public TestMove(String playerID, String actionType, int row, int col) {
        super(playerID, actionType, row, col);
    }
}

public class PlayerMoveTest {

    @Test
    void testMoveStoresValues() {
        PlayerMove move = new TestMove("p1", "MOVE", 1, 2);

        assertEquals("p1", move.getPlayerID());
        assertEquals("MOVE", move.getActionType());
        assertEquals(1, move.getRow());
        assertEquals(2, move.getCol());
    }
}