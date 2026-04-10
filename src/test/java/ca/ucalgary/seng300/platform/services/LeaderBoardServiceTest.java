package ca.ucalgary.seng300.platform.services;

import ca.ucalgary.seng300.platform.domain.PlayerStats;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaderBoardServiceTest {

    @Test
    void sortEntriesOrdersByRankScoreGamesThenUserId() {
        PlayerStats p1 = new PlayerStats(1, 1, 200); // gold-ish
        PlayerStats p2 = new PlayerStats(2, 1, 100); // gold-ish, higher games
        PlayerStats p3 = new PlayerStats(3, 1, 300); // silver-ish

        for (int i = 0; i < 15; i++) {
            p1.updateStatsWin(2.0); // score 30, games 15
            p2.updateStatsWin(2.0); // score 30, games 15
            p3.updateStatsWin(1.2); // score 18, games 15
        }
        p2.updateStatsWin(0.0); // same score, more games => should come first among p1/p2

        List<PlayerStats> entries = new ArrayList<>(List.of(p1, p2, p3));
        LeaderBoardService.sortEntries(entries);

        assertEquals(100, entries.get(0).getUserID());
        assertEquals(200, entries.get(1).getUserID());
        assertEquals(300, entries.get(2).getUserID());
    }
}
