package ca.ucalgary.seng300.platform.services;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MatchMakingServiceTest {

    private MatchMakingService queue;
    private final int TIC_TAC_TOE = Game.TICK_TACK_TOE;

    @BeforeEach
    void setUp() {
        queue = new MatchMakingService();
        // Clear the static queue to ensure test isolation
        MatchMakingService.queues.clear();
    }

    // tests if players are being added to the queue successfully
    @Test
    void testAddPlayerToQueue() {
        // Constructor: id=1, gameID=TIC_TAC_TOE, userID=1
        PlayerStats player = new PlayerStats(1, TIC_TAC_TOE, 1);
        player.updateStats(100); // Set score to 100

        queue.addPlayerToQueue(player);

        assertEquals(1, queue.getQueueSize(TIC_TAC_TOE));
        assertTrue(MatchMakingService.queues.get(TIC_TAC_TOE).contains(player));
    }

    // tests if matching players work
    @Test
    void testSuccessfulMatch() {
        PlayerStats player1 = new PlayerStats(1, TIC_TAC_TOE, 1);
        player1.updateStats(100);

        PlayerStats player2 = new PlayerStats(2, TIC_TAC_TOE, 2);
        player2.updateStats(105);

        queue.addPlayerToQueue(player1);
        queue.addPlayerToQueue(player2);

        List<PlayerStats> match = queue.matchPlayers(TIC_TAC_TOE);

        assertNotNull(match);
        assertEquals(2, match.size());
        assertEquals(0, queue.getQueueSize(TIC_TAC_TOE));
    }

    // tests if match making is respecting the 10 point threshold
    @Test
    void testMatchThresholdFailure(){
        PlayerStats player1 = new PlayerStats(1, TIC_TAC_TOE, 1);
        player1.updateStats(100);

        PlayerStats player2 = new PlayerStats(2, TIC_TAC_TOE, 2);
        player2.updateStats(111);

        queue.addPlayerToQueue(player1);
        queue.addPlayerToQueue(player2);

        List<PlayerStats> match = queue.matchPlayers(TIC_TAC_TOE);

        assertNull(match);
        assertEquals(2, queue.getQueueSize(TIC_TAC_TOE));
    }

    @Test
    void testClosestMatchLogic() {
        PlayerStats player1 = new PlayerStats(1, TIC_TAC_TOE, 1);
        player1.updateStats(100);

        PlayerStats player2 = new PlayerStats(2, TIC_TAC_TOE, 2);
        player2.updateStats(150);

        PlayerStats player3 = new PlayerStats(3, TIC_TAC_TOE, 3);
        player3.updateStats(105);

        queue.addPlayerToQueue(player1);
        queue.addPlayerToQueue(player2);
        queue.addPlayerToQueue(player3);

        List<PlayerStats> match = queue.matchPlayers(TIC_TAC_TOE);

        assertTrue(match.contains(player1));
        assertTrue(match.contains(player3));
        assertFalse(match.contains(player2));
    }

    @Test
    void testMatchWithEmptyQueue() {
        List<PlayerStats> match = queue.matchPlayers(TIC_TAC_TOE);
        assertNull(match);
    }
}
