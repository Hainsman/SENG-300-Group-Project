package ca.ucalgary.seng300.platform.stubs;

import ca.ucalgary.seng300.model.GeneralStats;
import ca.ucalgary.seng300.model.Rank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneralStatsTest {

    @Test
    void testConstructorInitialValues() {
        GeneralStats stats = new GeneralStats(1);

        assertEquals(1, stats.getID());
        assertEquals(0, stats.getGamesPlayed());
        assertEquals(0, stats.getWins());
        assertEquals(0, stats.getLosses());
        assertEquals(0, stats.getTies());
        assertEquals(0.0, stats.getMmr());
        assertEquals(Rank.BRONZE, stats.getRank());
    }

    @Test
    void testWinUpdatesStatsCorrectly() {
        GeneralStats stats = new GeneralStats(2);

        stats.win();

        assertEquals(1, stats.getGamesPlayed());
        assertEquals(1, stats.getWins());
        assertEquals(0, stats.getLosses());
        assertEquals(0, stats.getTies());
        assertEquals(1.0, stats.getMmr());
        assertEquals(Rank.BRONZE, stats.getRank());
    }

    @Test
    void testLoseUpdatesStatsCorrectly() {
        GeneralStats stats = new GeneralStats(3);

        stats.lose();

        assertEquals(1, stats.getGamesPlayed());
        assertEquals(0, stats.getWins());
        assertEquals(1, stats.getLosses());
        assertEquals(0, stats.getTies());
        assertEquals(-1.0, stats.getMmr());
        assertEquals(Rank.BRONZE, stats.getRank());
    }

    @Test
    void testTieUpdatesStatsCorrectly() {
        GeneralStats stats = new GeneralStats(4);

        stats.tie();

        assertEquals(1, stats.getGamesPlayed());
        assertEquals(0, stats.getWins());
        assertEquals(0, stats.getLosses());
        assertEquals(1, stats.getTies());
        assertEquals(0.0, stats.getMmr());
        assertEquals(Rank.BRONZE, stats.getRank());
    }

    @Test
    void testUpdateMMRWin() {
        GeneralStats stats = new GeneralStats(5);

        stats.updateMMR(true);

        assertEquals(1.0, stats.getMmr());
    }

    @Test
    void testUpdateMMRLoss() {
        GeneralStats stats = new GeneralStats(6);

        stats.updateMMR(false);

        assertEquals(-1.0, stats.getMmr());
    }

    @Test
    void testRankStaysBronzeBeforeTenGames() {
        GeneralStats stats = new GeneralStats(7);

        for (int i = 0; i < 9; i++) {
            stats.win();
        }

        assertEquals(9, stats.getGamesPlayed());
        assertEquals(9.0, stats.getMmr());
        assertEquals(Rank.BRONZE, stats.getRank());
    }

    @Test
    void testRankBecomesBronzeAtTenGamesIfMMRTooLow() {
        GeneralStats stats = new GeneralStats(8);

        for (int i = 0; i < 10; i++) {
            stats.tie();
        }

        assertEquals(10, stats.getGamesPlayed());
        assertEquals(0.0, stats.getMmr());
        assertEquals(Rank.BRONZE, stats.getRank());
    }

    @Test
    void testRankBecomesSilverAfterTenGamesAndEnoughMMR() {
        GeneralStats stats = new GeneralStats(9);

        for (int i = 0; i < 14; i++) {
            stats.win();
        }

        assertEquals(14, stats.getGamesPlayed());
        assertEquals(14.0, stats.getMmr());
        assertEquals(Rank.SILVER, stats.getRank());
    }

    @Test
    void testRankBecomesGold() {
        GeneralStats stats = new GeneralStats(10);

        for (int i = 0; i < 29; i++) {
            stats.win();
        }

        assertEquals(29, stats.getGamesPlayed());
        assertEquals(29.0, stats.getMmr());
        assertEquals(Rank.GOLD, stats.getRank());
    }

    @Test
    void testRankBecomesGrandmaster() {
        GeneralStats stats = new GeneralStats(11);

        for (int i = 0; i < 89; i++) {
            stats.win();
        }

        assertEquals(89, stats.getGamesPlayed());
        assertEquals(89.0, stats.getMmr());
        assertEquals(Rank.GRANDMASTER, stats.getRank());
    }
}
