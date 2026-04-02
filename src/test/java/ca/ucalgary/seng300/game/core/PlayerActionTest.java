package ca.ucalgary.seng300.game.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestAction extends PlayerAction {
    public TestAction(String playerID, String actionType, int row, int col) {
        super(playerID, actionType, row, col);
    }
}

public class PlayerActionTest {

    @Test
    void testActionStoresValues() {
        PlayerAction action = new TestAction("p2", "PLACE", 0, 1);

        assertEquals("p2", action.getPlayerID());
        assertEquals("PLACE", action.getActionType());
        assertEquals(0, action.getRowValue());
        assertEquals(1, action.getColValue());
    }
}