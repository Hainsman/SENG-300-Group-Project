package ca.ucalgary.seng300.game.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StateTest {

    @Test
    void testStateValues() {
        assertEquals(State.IN_PROGRESS, State.valueOf("IN_PROGRESS"));
        assertEquals(State.TIE, State.valueOf("TIE"));
        assertEquals(State.PLAYER_1_WIN, State.valueOf("PLAYER_1_WIN"));
        assertEquals(State.PLAYER_2_WIN, State.valueOf("PLAYER_2_WIN"));
    }

    @Test
    void testStateCount() {
        assertEquals(4, State.values().length);
    }
}