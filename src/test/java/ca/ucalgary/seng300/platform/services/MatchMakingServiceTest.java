package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.constants.Game;
import ca.ucalgary.seng300.platform.domain.PlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MatchMakingServiceTest {

    private MatchMakingService service;

    @BeforeEach
    void setUp() {
        service = new MatchMakingService();
        MatchMakingService.queues.clear();
    }

    @Test
    void canMatchPairRejectsLargeRankGap() {
        PlayerStats bronzeish = new PlayerStats(1, Game.TICK_TACK_TOE, 10);
        for (int i = 0; i < 12; i++) bronzeish.updateStatsLose(0.0); // enough games, low score => BRONZE

        PlayerStats masterish = new PlayerStats(2, Game.TICK_TACK_TOE, 11);
        for (int i = 0; i < 20; i++) masterish.updateStatsWin(5.0); // high score => high rank

        assertFalse(service.canMatchPair(bronzeish, masterish));
    }

    @Test
    void canMatchPairRejectsLargeScoreGapWithinRankBand() {
        PlayerStats left = new PlayerStats(1, Game.CONNECT_FOUR, 20);
        PlayerStats right = new PlayerStats(2, Game.CONNECT_FOUR, 21);
        for (int i = 0; i < 15; i++) {
            left.updateStatsWin(1.0);   // score 15
            right.updateStatsWin(1.8);  // score 27
        }
        assertFalse(service.canMatchPair(left, right));
    }

    @Test
    void matchPlayersReturnsClosestValidPair() {
        PlayerStats a = new PlayerStats(1, Game.TICK_TACK_TOE, 30);
        PlayerStats b = new PlayerStats(2, Game.TICK_TACK_TOE, 31);
        PlayerStats c = new PlayerStats(3, Game.TICK_TACK_TOE, 32);
        for (int i = 0; i < 14; i++) {
            a.updateStatsWin(1.0); // 14
            b.updateStatsWin(1.0); // 14
            c.updateStatsWin(1.4); // 19.6
        }

        MatchMakingService.queues.put(Game.TICK_TACK_TOE, new ArrayList<>());
        service.addPlayerToQueue(a);
        service.addPlayerToQueue(b);
        service.addPlayerToQueue(c);

        var matched = service.matchPlayers(Game.TICK_TACK_TOE);
        assertNotNull(matched);
        assertEquals(2, matched.size());
        assertTrue(matched.stream().anyMatch(p -> p.getUserID() == 30));
        assertTrue(matched.stream().anyMatch(p -> p.getUserID() == 31));
    }
}
