package ca.ucalgary.seng300.game.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void testEnumValuesExist() {
        assertNotNull(Player.PLAYER_1);
        assertNotNull(Player.PLAYER_2);
        assertNotNull(Player.EMPTY);
    }

    @Test
    void testEnumCount() {
        assertEquals(3, Player.values().length);
    }
}