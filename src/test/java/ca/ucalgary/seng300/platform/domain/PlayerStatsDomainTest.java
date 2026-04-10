package ca.ucalgary.seng300.platform.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PlayerStats domain object.
 *
 * PlayerStats holds per-player statistics for a specific game:
 * gameID, userID, a game counter, and a cumulative score.
 * The only non-trivial logic is updateStats(double), which
 * increments the counter and accumulates the score in one call.
 */
class PlayerStatsDomainTest {

    private PlayerStats stats;

    /**
     * Fresh PlayerStats before each test — gameID=10, userID=42.
     * Counter and score start at their defaults (0 and 0.0).
     */
    @BeforeEach
    void setUp() {
        stats = new PlayerStats(1, 10, 42);
    }

    /**
     * The IDs passed to the constructor must be stored and returned
     * correctly by their respective getters.
     */
    @Test
    void constructor_setsGameIdAndUserIdCorrectly() {
        assertEquals(10, stats.getGameID());
        assertEquals(42, stats.getUserID());
    }

    /**
     * Counter must start at 0 — no games have been played yet.
     */
    @Test
    void constructor_counterDefaultsToZero() {
        assertEquals(0, stats.getGameCounter());
    }

    /**
     * Score must start at 0.0 — no score has been accumulated yet.
     */
    @Test
    void constructor_scoreDefaultsToZero() {
        assertEquals(0.0, stats.getScore(), 0.0001);
    }

    /**
     * A single updateStats() call must increment the counter by exactly
     * 1 and add the given value to the score.
     */
    @Test
    void updateStats_singleCall_incrementsCounterAndAddsScore() {
        stats.updateStats(5.0);

        assertEquals(1,   stats.getGameCounter());
        assertEquals(5.0, stats.getScore(), 0.0001);
    }

    /**
     * Multiple updateStats() calls must accumulate: counter grows by 1
     * per call and score is the running total of all values passed in.
     */
    @Test
    void updateStats_multipleCalls_accumulatesCorrectly() {
        stats.updateStats(3.0);
        stats.updateStats(2.5);
        stats.updateStats(4.0);

        assertEquals(3,    stats.getGameCounter());
        assertEquals(9.5,  stats.getScore(), 0.0001);
    }

    /**
     * updateStats() with a negative value (e.g. a penalty) must still
     * increment the counter while reducing the cumulative score.
     */
    @Test
    void updateStats_negativeValue_decrementsScore() {
        stats.updateStats(10.0);
        stats.updateStats(-3.0);

        assertEquals(2,   stats.getGameCounter());
        assertEquals(7.0, stats.getScore(), 0.0001);
    }
}
