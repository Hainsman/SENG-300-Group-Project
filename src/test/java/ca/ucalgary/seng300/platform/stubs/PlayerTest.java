package ca.ucalgary.seng300.platform.stubs;

import ca.ucalgary.seng300.model.Player;
import ca.ucalgary.seng300.model.Rank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerConstructorAndGetters() {
        Player player = new Player(
                "Alice",
                "alice@example.com",
                "password123",
                Rank.GOLD
        );

        assertEquals("Alice", player.getUsername());
        assertEquals("alice@example.com", player.getEmail());
        assertEquals("password123", player.getPassword());
        assertEquals(Rank.GOLD, player.getRanking());
    }

    @Test
    void testDifferentRank() {
        Player player = new Player(
                "Bob",
                "bob@example.com",
                "securePass",
                Rank.SILVER
        );

        assertEquals("Bob", player.getUsername());
        assertEquals("bob@example.com", player.getEmail());
        assertEquals("securePass", player.getPassword());
        assertEquals(Rank.SILVER, player.getRanking());
    }

    @Test
    void testPlayerWithBronzeRank() {
        Player player = new Player(
                "Charlie",
                "charlie@example.com",
                "charliePass",
                Rank.BRONZE
        );

        assertEquals("Charlie", player.getUsername());
        assertEquals("charlie@example.com", player.getEmail());
        assertEquals("charliePass", player.getPassword());
        assertEquals(Rank.BRONZE, player.getRanking());
    }
}