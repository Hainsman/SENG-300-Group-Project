package ca.ucalgary.seng300.platform.services;
import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class MatchMakingServiceTest {

    // tests if players are being added to the queue successfully
    @Test
    void testAddPlayerToQueue() {
        int TIC_TAC_TOE = Game.TICK_TACK_TOE;
        MatchMakingService queue = new MatchMakingService();

        PlayerStats player = new PlayerStats(1, TIC_TAC_TOE, 100);
        queue.addPlayerToQueue(player);

        assertEquals(1, queue.getQueueSize(TIC_TAC_TOE));
        assertTrue(MatchMakingService.queues.get(TIC_TAC_TOE).contains(player));
    }



}
