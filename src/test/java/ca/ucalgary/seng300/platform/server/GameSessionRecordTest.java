package ca.ucalgary.seng300.platform.server;
import ca.ucalgary.seng300.game.core.Player;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameSessionRecordTest {

    // Tests if adding first player works and also if session is full with just one player
    @Test
    void testAddFirstPlayer() {
        GameSessionRecord sessionRecord = new GameSessionRecord();
        String player1Id = "user123";

        boolean isFull = sessionRecord.addPlayer(player1Id);
        assertFalse(isFull);
        assertEquals(1, sessionRecord.getPlayerCount());

        Map<String, Player> players = sessionRecord.getPlayerIdToPlayer();
        //first player should be assigned Player1
        assertEquals(Player.PLAYER_1, players.get(player1Id));
    }

    // tests if adding second player works (session should be full then)
    @Test
    void testAddSecondPlayer() {
        GameSessionRecord sessionRecord = new GameSessionRecord();
        String player1Id = "user123";
        String player2Id = "user456";

        sessionRecord.addPlayer(player1Id);
        boolean isFull = sessionRecord.addPlayer(player2Id);

        assertTrue(isFull);
        //there should be two players in the session at this point
        assertEquals(2, sessionRecord.getPlayerCount());

        Map<String, Player> players = sessionRecord.getPlayerIdToPlayer();
        //second player should be assigned Player2
        assertEquals(Player.PLAYER_2, players.get(player2Id));
    }

    // tests if adding third player works
    @Test
    void testAddThirdlayer() {
        GameSessionRecord sessionRecord = new GameSessionRecord();
        String player1Id = "user123";
        String player2Id = "user456";
        String player3Id = "user789";

        sessionRecord.addPlayer(player1Id);
        sessionRecord.addPlayer(player2Id);
        boolean isAdded = sessionRecord.addPlayer(player3Id);

        assertFalse(isAdded);
        //there should still be only two players in the session at this point
        assertEquals(2, sessionRecord.getPlayerCount());
    }

}
