package ca.ucalgary.seng300.tests;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import ca.ucalgary.seng300.platform.services.MatchMakingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class MatchMakingTest {

    MatchMakingService matchMaking;

    PlayerStats mmPlayer1;
    PlayerStats mmPlayer2;
    PlayerStats mmPlayer3;
    PlayerStats mmPlayer4;
    PlayerStats mmPlayer5;
    PlayerStats mmPlayer6;
    PlayerStats mmPlayer7;
    PlayerStats mmPlayer8;

    // runs before every single test to give a clean state
    @BeforeEach
    void setUp() {
        matchMaking = new MatchMakingService();
        MatchMakingService.queues.clear();

        // mmPlayer1: score = 50.0  (10 x 5.0)
        // mmPlayer2: score = 53.0  (10 x 5.3)  <- diff from player1 = 3.0
        // mmPlayer3: score = 90.0  (10 x 9.0)  <- too far from everyone
        // mmPlayer4: score = 35.0  (10 x 3.5)
        // mmPlayer5: score = 35.1  (10 x 3.51) <- closest pair overall, diff = 0.1
        // mmPlayer6: score = 34.0  (10 x 3.4)  <- diff from player5 = 1.1, diff from player4 = 1.0
        // mmPlayer7: score = 50.0  (10 x 5.0)  ConnectFour
        // mmPlayer8: score = 55.0  (10 x 5.5)  ConnectFour
        mmPlayer1 = new PlayerStats(101, Game.TICK_TACK_TOE, 101);
        mmPlayer2 = new PlayerStats(102, Game.TICK_TACK_TOE, 102);
        mmPlayer3 = new PlayerStats(103, Game.TICK_TACK_TOE, 103);
        mmPlayer4 = new PlayerStats(104, Game.TICK_TACK_TOE, 104);
        mmPlayer5 = new PlayerStats(105, Game.TICK_TACK_TOE, 105);
        mmPlayer6 = new PlayerStats(106, Game.TICK_TACK_TOE, 106);
        mmPlayer7 = new PlayerStats(107, Game.CONNECT_FOUR, 107);
        mmPlayer8 = new PlayerStats(108, Game.CONNECT_FOUR, 108);

        for (int i = 0; i < 10; i++) mmPlayer1.updateStats(5.0);
        for (int i = 0; i < 10; i++) mmPlayer2.updateStats(5.3);
        for (int i = 0; i < 10; i++) mmPlayer3.updateStats(9.0);
        for (int i = 0; i < 10; i++) mmPlayer4.updateStats(3.5);
        for (int i = 0; i < 10; i++) mmPlayer5.updateStats(3.51);
        for (int i = 0; i < 10; i++) mmPlayer6.updateStats(3.4);
        for (int i = 0; i < 10; i++) mmPlayer7.updateStats(5.0);
        for (int i = 0; i < 10; i++) mmPlayer8.updateStats(5.5);
    }

    // --- Test 1: addPlayerToQueue adds players correctly ---
    @Test
    void testAddPlayerToQueue() {
        matchMaking.addPlayerToQueue(mmPlayer1);
        matchMaking.addPlayerToQueue(mmPlayer2);
        matchMaking.addPlayerToQueue(mmPlayer3);
        assertEquals(3, matchMaking.getQueueSize(Game.TICK_TACK_TOE));
    }

    // --- Test 2: removePlayer removes the correct player ---
    @Test
    void testRemovePlayer() {
        matchMaking.addPlayerToQueue(mmPlayer1);
        matchMaking.addPlayerToQueue(mmPlayer2);
        MatchMakingService.removePlayer(mmPlayer2);
        assertEquals(1, matchMaking.getQueueSize(Game.TICK_TACK_TOE));
    }

    // --- Test 3: matchPlayers returns the closest pair (not just first pair found) ---
    // queue has 35.0, 35.1, 34.0 — closest pair is 35.0 and 35.1 (diff=0.1)
    // NOT 35.0 and 34.0 (diff=1.0) or 35.1 and 34.0 (diff=1.1)
    @Test
    void testMatchPlayersReturnsClosestPair() {
        matchMaking.addPlayerToQueue(mmPlayer4); // 35.0
        matchMaking.addPlayerToQueue(mmPlayer5); // 35.1
        matchMaking.addPlayerToQueue(mmPlayer6); // 34.0
        List<PlayerStats> closestMatch = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        assertNotNull(closestMatch);
        assertEquals(2, closestMatch.size());
        assertEquals(104, closestMatch.get(0).getUserID());
        assertEquals(105, closestMatch.get(1).getUserID());
    }

    // --- Test 4: after match, only the unmatched player remains in queue ---
    @Test
    void testUnmatchedPlayerRemainsInQueue() {
        matchMaking.addPlayerToQueue(mmPlayer4); // 35.0
        matchMaking.addPlayerToQueue(mmPlayer5); // 35.1
        matchMaking.addPlayerToQueue(mmPlayer6); // 34.0
        matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        assertEquals(1, matchMaking.getQueueSize(Game.TICK_TACK_TOE));
    }

    // --- Test 5: matched players are removed from the queue ---
    @Test
    void testMatchedPlayersRemovedFromQueue() {
        matchMaking.addPlayerToQueue(mmPlayer1); // 50.0
        matchMaking.addPlayerToQueue(mmPlayer2); // 53.0
        List<PlayerStats> matched = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        assertNotNull(matched);
        assertEquals(2, matched.size());
        assertEquals(101, matched.get(0).getUserID());
        assertEquals(102, matched.get(1).getUserID());
        assertEquals(0, matchMaking.getQueueSize(Game.TICK_TACK_TOE));
    }
    // --- Test 7: matchPlayers on empty queue returns null ---
    @Test
    void testMatchPlayersOnEmptyQueueReturnsNull() {
        List<PlayerStats> emptyMatch = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        assertNull(emptyMatch);
    }

    // --- Test 8: single player in queue returns null ---
    @Test
    void testSinglePlayerInQueueReturnsNull() {
        matchMaking.addPlayerToQueue(mmPlayer1);
        List<PlayerStats> singlePlayer = matchMaking.matchPlayers(Game.TICK_TACK_TOE);
        assertNull(singlePlayer);
    }

    // --- Test 9: different game type queues don't interfere ---
    @Test
    void testDifferentGameTypeQueuesDoNotInterfere() {
        matchMaking.addPlayerToQueue(mmPlayer1); // TicTacToe
        matchMaking.addPlayerToQueue(mmPlayer7); // ConnectFour
        matchMaking.addPlayerToQueue(mmPlayer8); // ConnectFour
        assertEquals(1, matchMaking.getQueueSize(Game.TICK_TACK_TOE));
        assertEquals(2, matchMaking.getQueueSize(Game.CONNECT_FOUR));
    }

    // --- Test 10: removePlayer on unknown player does nothing ---
    @Test
    void testRemoveUnknownPlayerDoesNothing() {
        matchMaking.addPlayerToQueue(mmPlayer1);
        PlayerStats unknownPlayer = new PlayerStats(999, Game.TICK_TACK_TOE, 999);
        MatchMakingService.removePlayer(unknownPlayer);
        assertEquals(1, matchMaking.getQueueSize(Game.TICK_TACK_TOE));
    }

    // --- Test 11: matching in ConnectFour queue works independently ---
    @Test
    void testConnectFourMatchingWorksIndependently() {
        matchMaking.addPlayerToQueue(mmPlayer7); // ConnectFour score 50.0
        matchMaking.addPlayerToQueue(mmPlayer8); // ConnectFour score 55.0, diff = 5.0
        List<PlayerStats> connectFourMatch = matchMaking.matchPlayers(Game.CONNECT_FOUR);
        assertNotNull(connectFourMatch);
        assertEquals(2, connectFourMatch.size());
        assertEquals(107, connectFourMatch.get(0).getUserID());
        assertEquals(108, connectFourMatch.get(1).getUserID());
    }


}